package com.company.consumables.ai;

import com.company.consumables.ai.intent.IntentHandler;
import com.company.consumables.ai.intent.IntentRouter;
import com.company.consumables.ai.vo.AiAnswerVo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类描述: 意图路由单元测试。验证按 code 分发、未知意图无处理器、意图清单包含全部意图（Property 5）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
class IntentRouterTest {

    /**
     * 功能描述: 构造一个假的意图处理器
     *
     * @param code 意图编码
     * @param desc 说明
     * @return 处理器
     * @author honghui
     * @date 2026/07/08 13:00
     */
    private IntentHandler fakeHandler(String code, String desc) {
        return new IntentHandler() {
            @Override
            public String intentCode() {
                return code;
            }

            @Override
            public String description() {
                return desc;
            }

            @Override
            public String paramsHint() {
                return "";
            }

            @Override
            public AiAnswerVo handle(Map<String, Object> params) {
                return AiAnswerVo.success(code, "ok", null);
            }
        };
    }

    /**
     * 功能描述: 按意图编码正确分发到对应处理器
     *
     * @author honghui
     * @date 2026/07/08 13:00
     */
    @Test
    void routesByIntentCode() {
        IntentRouter router = new IntentRouter(Arrays.asList(
                fakeHandler("SALES_AMOUNT", "销售额"),
                fakeHandler("STOCK_QUERY", "库存")));

        assertNotNull(router.getHandler("SALES_AMOUNT"));
        assertEquals("SALES_AMOUNT", router.getHandler("SALES_AMOUNT").intentCode());
        assertTrue(router.contains("STOCK_QUERY"));
    }

    /**
     * 功能描述: 未知意图无处理器、不命中（拒答的基础）
     *
     * @author honghui
     * @date 2026/07/08 13:00
     */
    @Test
    void unknownIntentReturnsNull() {
        IntentRouter router = new IntentRouter(Arrays.asList(
                fakeHandler("SALES_AMOUNT", "销售额")));

        assertNull(router.getHandler("NOT_EXIST"));
        assertNull(router.getHandler(null));
        assertFalse(router.contains("NOT_EXIST"));
    }

    /**
     * 功能描述: 意图清单包含全部已注册意图，拒答范围提示包含说明
     *
     * @author honghui
     * @date 2026/07/08 13:00
     */
    @Test
    void catalogContainsAllIntents() {
        IntentRouter router = new IntentRouter(Arrays.asList(
                fakeHandler("SALES_AMOUNT", "销售额"),
                fakeHandler("STOCK_QUERY", "库存概览")));

        String catalog = router.buildIntentCatalog();
        assertTrue(catalog.contains("SALES_AMOUNT"));
        assertTrue(catalog.contains("STOCK_QUERY"));

        String scope = router.buildScopeHint();
        assertTrue(scope.contains("销售额"));
        assertTrue(scope.contains("库存概览"));
    }
}
