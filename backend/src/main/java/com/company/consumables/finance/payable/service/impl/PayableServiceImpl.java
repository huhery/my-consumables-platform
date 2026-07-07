package com.company.consumables.finance.payable.service.impl;

import com.company.consumables.common.enums.FundDirection;
import com.company.consumables.common.enums.FundFlowType;
import com.company.consumables.common.enums.SettleStatus;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.finance.fund.service.FundFlowService;
import com.company.consumables.finance.payable.entity.Payable;
import com.company.consumables.finance.payable.mapper.PayableMapper;
import com.company.consumables.finance.payable.service.PayableService;
import com.company.consumables.finance.payable.vo.PayableQueryVo;
import com.company.consumables.finance.payable.vo.PayableSummaryVo;
import com.company.consumables.finance.payable.vo.PaymentSaveVo;
import com.company.consumables.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 类描述: 应付账款与采购付款服务实现（与应收镜像）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@Service
@RequiredArgsConstructor
public class PayableServiceImpl implements PayableService {

    /** 免登录场景默认操作人 */
    @Value("${consumables.audit.default-user:admin}")
    private String defaultUser;

    private final PayableMapper payableMapper;
    private final FundFlowService fundFlowService;

    /**
     * 功能描述: 由进货单生成应付账款
     *
     * @param purchaseId  进货单ID
     * @param supplierId  供应商ID
     * @param totalAmount 应付金额（分）
     * @author honghui
     * @date 2026/07/06 11:15
     */
    @Override
    public void generateFromPurchase(String purchaseId, String supplierId, int totalAmount) {
        Payable payable = new Payable();
        payable.setSPurchaseId(purchaseId);
        payable.setSSupplierId(supplierId);
        payable.setITotalAmount(totalAmount);
        payable.setIPaidAmount(0);
        payable.setIStatus(SettleStatus.UNSETTLED.getCode());
        payableMapper.insert(payable);
    }

    /**
     * 功能描述: 登记采购付款。关联进货单时冲减未付并校验超额，未关联时作为供应商整体付款
     *
     * @param vo 付款入参
     * @author honghui
     * @date 2026/07/06 11:15
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPayment(PaymentSaveVo vo) {
        if (vo.getIAmount() == null || vo.getIAmount() <= 0) {
            throw new BusinessException(ErrorCode.AMOUNT_INVALID);
        }
        if (StringUtils.hasText(vo.getSPurchaseId())) {
            Payable payable = payableMapper.selectByPurchaseId(vo.getSPurchaseId());
            if (payable == null) {
                throw new BusinessException(ErrorCode.PAYABLE_NOT_FOUND);
            }
            int unpaid = payable.getITotalAmount() - payable.getIPaidAmount();
            if (vo.getIAmount() > unpaid) {
                throw new BusinessException(ErrorCode.PAYMENT_OVER_AMOUNT);
            }
            int newPaid = payable.getIPaidAmount() + vo.getIAmount();
            int status = newPaid >= payable.getITotalAmount()
                    ? SettleStatus.SETTLED.getCode() : SettleStatus.UNSETTLED.getCode();
            payableMapper.updatePaid(payable.getSId(), newPaid, status, resolveOperator());
        }
        // 写采购付款资金流水（支出方向）
        fundFlowService.record(FundFlowType.PURCHASE_PAYMENT, FundDirection.EXPENSE, vo.getIAmount(),
                vo.getSSupplierId(), vo.getSPurchaseId(), null, vo.getDOccurDate(), vo.getSRemark());
    }

    /**
     * 功能描述: 分页查询应付
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 11:15
     */
    @Override
    public PageResult<Payable> pagePayable(PayableQueryVo query) {
        long total = payableMapper.countPage(query);
        List<Payable> list = payableMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 采购付款汇总
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 11:15
     */
    @Override
    public List<PayableSummaryVo> summary() {
        return payableMapper.summaryBySupplier();
    }

    /**
     * 功能描述: 查询某供应商应付明细
     *
     * @param supplierId 供应商ID
     * @return 应付列表
     * @author honghui
     * @date 2026/07/06 11:15
     */
    @Override
    public List<Payable> listBySupplier(String supplierId) {
        return payableMapper.selectBySupplierId(supplierId);
    }

    /**
     * 功能描述: 解析当前操作人，无上下文时取默认用户
     *
     * @return 操作人
     * @author honghui
     * @date 2026/07/06 13:12
     */
    private String resolveOperator() {
        String currentUser = UserContext.getCurrentUser();
        return StringUtils.hasText(currentUser) ? currentUser : defaultUser;
    }
}
