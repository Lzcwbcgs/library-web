package com.xiazihan.webback.common.constant;

public interface Constants {
    /**
     * JWT相关常量
     */
    interface JWT {
        String TOKEN_PREFIX = "Bearer ";
        String HEADER = "Authorization";
    }

    /**
     * 用户角色
     */
    interface UserRole {
        String ROLE_READER = "ROLE_READER";
        String ROLE_ADMIN = "ROLE_ADMIN";
        String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    }

    /**
     * 通用状态
     */
    interface Status {
        Integer ENABLED = 1;
        Integer DISABLED = 0;
    }

    /**
     * 借阅相关常量
     */
    interface Borrow {
        Integer MAX_BORROW_COUNT = 5;  // 最大借阅数量
        Integer MAX_BORROW_DAYS = 30;  // 最大借阅天数
        Integer MAX_RENEW_COUNT = 1;   // 最大续借次数
        Double OVERDUE_FINE_PER_DAY = 0.5;  // 每日逾期罚款金额
    }
} 