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
        static final class Role {
            public static final String NAME = "role_id";
        }

        static final class Company {
            public static final String NAME = "company_id";
        }
    }

    public static final class RoleTable {
        public static final String NAME = "role_table";
        public static final class Id{
            public static final String NAME = "role_id";
        }
        public static final class Role {
            public static final String NAME = "role";
        }
        public static final class Description {
            public static final String NAME = "role_description";
            public static final int LENGTH = 250;
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
    public static final class ClientTable{
        public static final String NAME= "client_table";
        public static final class Id{
            public static final String NAME = "client_id";
        }
        public static final class Name{
            public static final String NAME = "client_name";
            public static final int LENGTH = 50;
        }
        public static final class Phone{
            public static final String NAME = "client_phone";
        }
        public static final class Email{
            public static final String NAME = "client_email";
            public static final int LENGTH = 50;
        }
        public static final class Address{
            public static final String NAME = "client_address";
            public static final int LENGTH = 250;
        }
        public static final class State{
            public static final String NAME="client_state";
        }
        public static final class Date{
            public static final String NAME="client_createDate";
        }

    }
    public static final class ChargeTable{
        public static final String NAME = "charge_table";
        public static final class Id{
            public static final String NAME = "charge_id";
        }
        public static final class Description{
            public static final String NAME = "charge_description";
            public static final int LENGTH = 250;
        }
        public static final class Date{
            public static final String NAME = "charge_date";
        }
        public static final class Type{
            public static final String NAME = "charge_type";
        }
        public static final class Truck{
            public static final String NAME = "truck_id";
        }
        public static final class Client{
            public static final String NAME = "client_id";
        }
        public static final class State{
            public static final String NAME = "charge_state";
        }
        public static final class Contract{
            public static final String NAME = "contract_id";
        }
    }
    public static final class Container{
        public static final String NAME = "container_table";
        public static final class Id{
            public static final String NAME = "container_id";
        }
        public static final class Code{
            public static final String NAME = "container_code";
            public static final int LENGTH = 20;
        }
        public static final class Description{
            public static final String NAME = "container_description";
            public static final int LENGTH = 250;
        }
        public static final class Type{
            public static final String NAME = "container_type";
            public static final int LENGTH = 50;
        }
        public static final class State{
            public static final String NAME = "container_state";
        }
        public static final class Date{
            public static final String NAME = "container_date";
        }
        public static final class Weight{
            public static final String NAME = "container_weight";
        }
        public static final class Charge{
            public static final String NAME = "charge_id";
        }
    }

    public static final class JourneyTable{
        public static final String NAME = "journey_table";
        public static final class Id{
            public static final String NAME = "journey_id";
        }
        public static final class Description{
            public static final String NAME = "journey_description";
            public static final int LENGTH = 250;
        }
        public static final class DateStart{
            public static final String NAME = "journey_start_date";
        }
        public static final class DateEnd{
            public static final String NAME = "journey_end_date";
        }
        public static final class Type{
            public static final String NAME = "journey_type";
            public static final int LENGTH = 50;
        }
        public static final class StateJourney{
            public static final String NAME = "journey_state";
        }
        public static final class Truck{
            public static final String NAME = "truck_id";
        }
        public static final class Client{
            public static final String NAME = "client_id";
        }
        public static final class Container{
            public static final String NAME = "container_id";
        }
        public static final class Charge{
            public static final String NAME = "charge_id";
        }
        public static final class User{
            public static final String NAME = "user_id";
        }
        public static final class Company{
            public static final String NAME = "company_id";
        }
        public static final class State{
            public static final String NAME = "journey_state_table";
        }
        public static final class Origin{
            public static final String NAME = "journey_origin";
            public static final int LENGTH = 250;
        }
        public static final class Destination{
            public static final String NAME = "journey_destination";
            public static final int LENGTH = 250;
        }
        public static final class Contract{
            public static final String NAME = "contract_id";
        }
    }
    public static final class ContractTable{
        public static final String NAME = "contract_table";
        public static final class Id{
            public static final String NAME = "contract_id";
        }
        public static final class Description{
            public static final String NAME = "contract_description";
            public static final int LENGTH = 250;
        }
        public static final class Type{
            public static final String NAME = "contract_type";
            public static final int LENGTH = 50;
        }
        public static final class State{
            public static final String NAME = "contract_state";
        }
        public static final class Date{
            public static final String NAME = "contract_date";
        }
        public static final class Amount{
            public static final String NAME = "contract_amount";
        }
        public static final class Journey{
            public static final String NAME = "journey_id";
        }
        public static final class User{
            public static final String NAME = "user_id";
        }
        public static final class Company{
            public static final String NAME = "company_id";
        }
    }

}
