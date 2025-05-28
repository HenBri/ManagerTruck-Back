package com.manager.truck.truck.domain;

public final class Constants {
    public static final class User {
        public static final String NAME = "user_Table";
        public static final class Id{
            public static final String NAME = "user_id";
        }
        public static final class name{
            public static final String NAME = "user_name";
            public static final int LENGTH = 50;
        }
        public static final class lastname{
            public static final String NAME = "user_lastname";
            public static final int LENGTH = 200;
        }
        public static final class email{
            public static final String NAME = "user_email";
        }
        public static final class phone{
            public static final String NAME = "user_phone";
        }
        public static final class ci{
            public static final String NAME = "user_ci";
            public static final int LENGTH = 15;
        }
        public static final class role{
            public static final String NAME = "user_role";
        }
        public static final class state{
            public static final String NAME = "user_state";
        }
        public static final class createDate{
            public static final String NAME = "user_create_date";
        }
        public static final class updateDate{
            public static final String NAME = "user_update_date";
        }
        public static final class password{
            public static final String NAME = "password";
            public static final int LENGTH = 100;
        }

        public static final class company{
            public static final String NAME = "company_id";
        }

    }

    public static final class Company{
        public static final String NAME = "company_Table";
        public static final class Id{
            public static final String NAME = "company_id";
        }
        public static final class name{
            public static final String NAME = "company_name";
            public static final int LENGTH = 50;
        }
        public static final class address{
            public static final String NAME = "company_address";
            public static final int LENGTH = 250;
        }
        public static final class phone{
            public static final String NAME = "company_phone";
        }
        public static final class email{
            public static final String NAME = "company_email";
            public static final int LENGTH = 50;
        }
        public static final class state{
            public static final String NAME = "company_state";
        }
        public static final class createDate{
            public static final String NAME = "company_create_date";
        }

    }

}
