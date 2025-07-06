package com.manager.truck.truck.domain;

public final class Constants {
    public static final class UserTable {
        static final String NAME = "user_Table";
        static final class Id{
            public static final String NAME = "user_id";
        }
        static final class Name {
            public static final String NAME = "user_name";
            public static final int LENGTH = 50;
        }
        static final class Lastname {
            public static final String NAME = "user_lastname";
            public static final int LENGTH = 200;
        }
        static final class Email {
            public static final String NAME = "user_email";
        }
        static final class Phone {
            public static final String NAME = "user_phone";
        }
        static final class Ci {
            public static final String NAME = "user_ci";
            public static final int LENGTH = 15;
        }
        static final class Role {
            public static final String NAME = "user_role";
        }
        static final class State {
            public static final String NAME = "user_state";
        }
        static final class CreateDate {
            public static final String NAME = "user_create_date";
        }
        static final class UpdateDate {
            public static final String NAME = "user_update_date";
        }
        static final class Password {
            public static final String NAME = "password";
            public static final int LENGTH = 100;
        }

        static final class Company {
            public static final String NAME = "company_id";
        }
    }

    public static final class CompanyTable {
        public static final String NAME = "company_Table";
        public static final class Id{
            public static final String NAME = "company_id";
        }
        public static final class Name {
            public static final String NAME = "company_name";
            public static final int LENGTH = 50;
        }
        public static final class Address {
            public static final String NAME = "company_address";
            public static final int LENGTH = 250;
        }
        public static final class Phone {
            public static final String NAME = "company_phone";
        }
        public static final class Email {
            public static final String NAME = "company_email";
            public static final int LENGTH = 50;
        }
        public static final class State {
            public static final String NAME = "company_state";
        }
        public static final class CreateDate {
            public static final String NAME = "company_create_date";
        }

    }

    public static final class TruckTable {
        public static final String NAME = "truck_table";
        public static final class Id{
            public static final String NAME = "truck_id";
        }
        public static final class Plate {
            public static final String NAME = "truck_plate";
            public static final int LENGTH = 20;
        }
        public static final class Model {
            public static final String NAME = "truck_model";
            public static final int LENGTH = 20;
        }
        public static final class Color {
            public static final String NAME = "truck_color";
            public static final int LENGTH = 20;
        }
        public static final class Brand {
            public static final String NAME = "truck_brand";
            public static final int LENGTH = 20;
        }
        public static final class Chassis {
            public static final String NAME = "truck_chassis";
            public static final int LENGTH = 20;
        }
        public static final class State {
            public static final String NAME = "truck_state";
        }
        public static final class CreateDate{
            public static final String NAME = "truck_createDate";
        }
        static final class User{
            static final String NAME = "user_id";
        }
    }

}
