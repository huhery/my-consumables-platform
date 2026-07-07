package com.company.consumables.finance.receivable.service.impl;

import com.company.consumables.common.enums.FundDirection;
import com.company.consumables.common.enums.FundFlowType;
import com.company.consumables.common.enums.SettleStatus;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.finance.fund.service.FundFlowService;
import com.company.consumables.finance.receivable.entity.Receivable;
import com.company.consumables.finance.receivable.mapper.ReceivableMapper;
import com.company.consumables.finance.receivable.service.ReceivableService;
import com.company.consumables.finance.receivable.vo.ReceiptSaveVo;
import com.company.consumables.finance.receivable.vo.ReceivableQueryVo;
import com.company.consumables.finance.receivable.vo.ReceivableSummaryVo;
import com.company.consumables.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 类描述: 应收账款与销售收款服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Service
@RequiredArgsConstructor
public class ReceivableServiceImpl implements ReceivableService {

    /** 免登录场景默认操作人 */
    @Value("${consumables.audit.default-user:admin}")
    private String defaultUser;

    private final ReceivableMapper receivableMapper;
    private final FundFlowService fundFlowService;

    /**
     * 功能描述: 由批发销售单生成应收账款
     *
     * @param saleId      销售单ID
     * @param customerId  客户ID
     * @param totalAmount 应收金额（分）
     * @author honghui
     * @date 2026/07/06 10:35
     */
    @Override
    public void generateFromSale(String saleId, String customerId, int totalAmount) {
        Receivable receivable = new Receivable();
        receivable.setSSaleId(saleId);
        receivable.setSCustomerId(customerId);
        receivable.setITotalAmount(totalAmount);
        receivable.setIReceivedAmount(0);
        receivable.setIStatus(SettleStatus.UNSETTLED.getCode());
        receivableMapper.insert(receivable);
    }

    /**
     * 功能描述: 登记销售收款。关联单据时冲减该单未收并校验超额，
     *           未关联时作为客户整体收款；无论是否关联都生成销售收款资金流水
     *
     * @param vo 收款入参
     * @author honghui
     * @date 2026/07/06 10:35
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReceipt(ReceiptSaveVo vo) {
        if (vo.getIAmount() == null || vo.getIAmount() <= 0) {
            throw new BusinessException(ErrorCode.AMOUNT_INVALID);
        }
        // 关联具体销售单：冲减该单未收金额
        if (StringUtils.hasText(vo.getSSaleId())) {
            Receivable receivable = receivableMapper.selectBySaleId(vo.getSSaleId());
            if (receivable == null) {
                throw new BusinessException(ErrorCode.RECEIVABLE_NOT_FOUND);
            }
            int unreceived = receivable.getITotalAmount() - receivable.getIReceivedAmount();
            if (vo.getIAmount() > unreceived) {
                throw new BusinessException(ErrorCode.RECEIPT_OVER_AMOUNT);
            }
            int newReceived = receivable.getIReceivedAmount() + vo.getIAmount();
            int status = newReceived >= receivable.getITotalAmount()
                    ? SettleStatus.SETTLED.getCode() : SettleStatus.UNSETTLED.getCode();
            receivableMapper.updateReceived(receivable.getSId(), newReceived, status, resolveOperator());
        }
        // 写销售收款资金流水（收入方向）
        fundFlowService.record(FundFlowType.SALE_RECEIPT, FundDirection.INCOME, vo.getIAmount(),
                vo.getSCustomerId(), vo.getSSaleId(), null, vo.getDOccurDate(), vo.getSRemark());
    }

    /**
     * 功能描述: 分页查询应收
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 10:35
     */
    @Override
    public PageResult<Receivable> pageReceivable(ReceivableQueryVo query) {
        long total = receivableMapper.countPage(query);
        List<Receivable> list = receivableMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 销售欠款汇总
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 10:35
     */
    @Override
    public List<ReceivableSummaryVo> summary() {
        return receivableMapper.summaryByCustomer();
    }

    /**
     * 功能描述: 查询某客户应收明细
     *
     * @param customerId 客户ID
     * @return 应收列表
     * @author honghui
     * @date 2026/07/06 10:35
     */
    @Override
    public List<Receivable> listByCustomer(String customerId) {
        return receivableMapper.selectByCustomerId(customerId);
    }

    /**
     * 功能描述: 解析当前操作人，无上下文时取默认用户
     *
     * @return 操作人
     * @author honghui
     * @date 2026/07/06 13:10
     */
    private String resolveOperator() {
        String currentUser = UserContext.getCurrentUser();
        return StringUtils.hasText(currentUser) ? currentUser : defaultUser;
    }
}
