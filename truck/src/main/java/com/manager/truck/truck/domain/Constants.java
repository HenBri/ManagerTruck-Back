package com.manager.truck.truck.domain;

public final class Constants {

    private Constants() {
    }

    public static final class CompanyTable {
        public static final String NAME = "company";

        public static final class Id {
            public static final String NAME = "company_id";
        }

        public static final class Name {
            public static final String NAME = "company_name";
            public static final int LENGTH = 150;
        }

        public static final class Nit {
            public static final String NAME = "company_nit";
            public static final int LENGTH = 30;
        }

        public static final class Address {
            public static final String NAME = "company_address";
            public static final int LENGTH = 255;
        }

        public static final class Phone {
            public static final String NAME = "company_phone";
            public static final int LENGTH = 25;
        }

        public static final class Status {
            public static final String NAME = "company_status";
        }

        public static final class CreatedAt {
            public static final String NAME = "company_created_at";
        }
    }

    public static final class UserTable {
        public static final String NAME = "user_table";

        public static final class Id {
            public static final String NAME = "user_id";
        }

        public static final class Email {
            public static final String NAME = "user_email";
            public static final int LENGTH = 255;
        }

        public static final class PasswordHash {
            public static final String NAME = "password_hash";
            public static final int LENGTH = 255;
        }

        public static final class Name {
            public static final String NAME = "user_name";
            public static final int LENGTH = 100;
        }

        public static final class Lastname {
            public static final String NAME = "user_lastname";
            public static final int LENGTH = 150;
        }

        public static final class Ci {
            public static final String NAME = "user_ci";
            public static final int LENGTH = 30;
        }

        public static final class Phone {
            public static final String NAME = "user_phone";
            public static final int LENGTH = 25;
        }

        public static final class Status {
            public static final String NAME = "user_status";
        }

        public static final class CreatedAt {
            public static final String NAME = "user_created_at";
        }
    }

    public static final class RoleTable {
        public static final String NAME = "role_table";

        public static final class Id {
            public static final String NAME = "role_id";
        }

        public static final class Name {
            public static final String NAME = "role_name";
            public static final int LENGTH = 50;
        }

        public static final class Scope {
            public static final String NAME = "role_scope";
        }

        public static final class Description {
            public static final String NAME = "role_description";
            public static final int LENGTH = 255;
        }
    }

    public static final class CompanyUserTable {
        public static final String NAME = "company_user";

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class UserId {
            public static final String NAME = "user_id";
        }

        public static final class MembershipStatus {
            public static final String NAME = "membership_status";
        }

        public static final class JoinedAt {
            public static final String NAME = "joined_at";
        }
    }

    public static final class CompanyUserRoleTable {
        public static final String NAME = "company_user_role";

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class UserId {
            public static final String NAME = "user_id";
        }

        public static final class RoleId {
            public static final String NAME = "role_id";
        }

        public static final class RoleScope {
            public static final String NAME = "role_scope";
        }
    }

    public static final class PlatformUserRoleTable {
        public static final String NAME = "platform_user_role";

        public static final class UserId {
            public static final String NAME = "user_id";
        }

        public static final class RoleId {
            public static final String NAME = "role_id";
        }

        public static final class RoleScope {
            public static final String NAME = "role_scope";
        }

        public static final class GrantedAt {
            public static final String NAME = "granted_at";
        }
    }

    public static final class PartnerTable {
        public static final String NAME = "partner_table";

        public static final class Id {
            public static final String NAME = "partner_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class UserId {
            public static final String NAME = "user_id";
        }

        public static final class Type {
            public static final String NAME = "partner_type";
        }

        public static final class Name {
            public static final String NAME = "partner_name";
            public static final int LENGTH = 150;
        }

        public static final class Document {
            public static final String NAME = "partner_document";
            public static final int LENGTH = 30;
        }

        public static final class Phone {
            public static final String NAME = "partner_phone";
            public static final int LENGTH = 25;
        }

        public static final class State {
            public static final String NAME = "partner_state";
        }
    }

    public static final class DriverTable {
        public static final String NAME = "driver_table";

        public static final class Id {
            public static final String NAME = "driver_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class UserId {
            public static final String NAME = "user_id";
        }

        public static final class PersonPartnerId {
            public static final String NAME = "person_partner_id";
        }

        public static final class Name {
            public static final String NAME = "driver_name";
            public static final int LENGTH = 150;
        }

        public static final class Ci {
            public static final String NAME = "driver_ci";
            public static final int LENGTH = 30;
        }

        public static final class Phone {
            public static final String NAME = "driver_phone";
            public static final int LENGTH = 25;
        }

        public static final class License {
            public static final String NAME = "driver_license";
            public static final int LENGTH = 40;
        }

        public static final class LicenseCategory {
            public static final String NAME = "license_category";
            public static final int LENGTH = 20;
        }

        public static final class LicenseExpiration {
            public static final String NAME = "license_expiration";
        }

        public static final class State {
            public static final String NAME = "driver_state";
        }
    }

    public static final class TruckTable {
        public static final String NAME = "truck_table";

        public static final class Id {
            public static final String NAME = "truck_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class OwnerPartnerId {
            public static final String NAME = "owner_partner_id";
        }

        public static final class Board {
            public static final String NAME = "truck_board";
            public static final int LENGTH = 20;
        }

        public static final class Brand {
            public static final String NAME = "truck_brand";
            public static final int LENGTH = 50;
        }

        public static final class Model {
            public static final String NAME = "truck_model";
            public static final int LENGTH = 50;
        }

        public static final class Chassis {
            public static final String NAME = "truck_chassis";
            public static final int LENGTH = 50;
        }

        public static final class Color {
            public static final String NAME = "truck_color";
            public static final int LENGTH = 30;
        }

        public static final class Year {
            public static final String NAME = "truck_year";
        }

        public static final class Capacity {
            public static final String NAME = "truck_capacity";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class State {
            public static final String NAME = "truck_state";
        }

        public static final class CreateDate {
            public static final String NAME = "truck_create_date";
        }
    }

    public static final class ClientTable {
        public static final String NAME = "client";

        public static final class Id {
            public static final String NAME = "client_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class Name {
            public static final String NAME = "client_name";
            public static final int LENGTH = 150;
        }

        public static final class Ci {
            public static final String NAME = "client_ci";
            public static final int LENGTH = 30;
        }

        public static final class Email {
            public static final String NAME = "client_email";
            public static final int LENGTH = 255;
        }

        public static final class Phone {
            public static final String NAME = "client_phone";
            public static final int LENGTH = 25;
        }

        public static final class Address {
            public static final String NAME = "client_address";
            public static final int LENGTH = 255;
        }

        public static final class State {
            public static final String NAME = "client_state";
        }

        public static final class CreateDate {
            public static final String NAME = "client_create_date";
        }
    }

    public static final class ContractTable {
        public static final String NAME = "contract_table";

        public static final class Id {
            public static final String NAME = "contract_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class ClientId {
            public static final String NAME = "client_id";
        }

        public static final class Amount {
            public static final String NAME = "contract_amount";
            public static final int PRECISION = 15;
            public static final int SCALE = 2;
        }

        public static final class Date {
            public static final String NAME = "contract_date";
        }

        public static final class Description {
            public static final String NAME = "contract_description";
            public static final int LENGTH = 255;
        }

        public static final class Type {
            public static final String NAME = "contract_type";
            public static final int LENGTH = 50;
        }

        public static final class State {
            public static final String NAME = "contract_state";
        }
    }

    public static final class ChargeTable {
        public static final String NAME = "charge_table";

        public static final class Id {
            public static final String NAME = "charge_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class ContractId {
            public static final String NAME = "contract_id";
        }

        public static final class Description {
            public static final String NAME = "charge_description";
            public static final int LENGTH = 255;
        }

        public static final class Date {
            public static final String NAME = "charge_date";
        }

        public static final class Type {
            public static final String NAME = "charge_type";
            public static final int LENGTH = 100;
        }

        public static final class State {
            public static final String NAME = "charge_state";
        }
    }

    public static final class ContainerTable {
        public static final String NAME = "container_table";

        public static final class Id {
            public static final String NAME = "container_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class ChargeId {
            public static final String NAME = "charge_id";
        }

        public static final class Code {
            public static final String NAME = "container_code";
            public static final int LENGTH = 30;
        }

        public static final class Weight {
            public static final String NAME = "container_weight";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class Description {
            public static final String NAME = "container_description";
            public static final int LENGTH = 255;
        }

        public static final class Type {
            public static final String NAME = "container_type";
            public static final int LENGTH = 50;
        }

        public static final class State {
            public static final String NAME = "container_state";
        }

        public static final class Date {
            public static final String NAME = "container_date";
        }
    }

    public static final class TruckDriverAssignmentTable {
        public static final String NAME = "truck_driver_assignment";

        public static final class Id {
            public static final String NAME = "assignment_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class TruckId {
            public static final String NAME = "truck_id";
        }

        public static final class DriverId {
            public static final String NAME = "driver_id";
        }

        public static final class AssignedFrom {
            public static final String NAME = "assigned_from";
        }

        public static final class AssignedUntil {
            public static final String NAME = "assigned_until";
        }
    }

    public static final class AuxiliaryProviderTable {
        public static final String NAME = "auxiliary_provider";

        public static final class Id {
            public static final String NAME = "provider_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class Kind {
            public static final String NAME = "provider_kind";
        }

        public static final class Name {
            public static final String NAME = "provider_name";
            public static final int LENGTH = 150;
        }

        public static final class Document {
            public static final String NAME = "provider_document";
            public static final int LENGTH = 30;
        }

        public static final class Phone {
            public static final String NAME = "provider_phone";
            public static final int LENGTH = 25;
        }

        public static final class Email {
            public static final String NAME = "provider_email";
            public static final int LENGTH = 255;
        }

        public static final class Status {
            public static final String NAME = "provider_status";
        }

        public static final class CreatedAt {
            public static final String NAME = "created_at";
        }
    }

    public static final class JourneyTable {
        public static final String NAME = "journey_table";

        public static final class Id {
            public static final String NAME = "journey_id";
        }

        public static final class CompanyId {
            public static final String NAME = "company_id";
        }

        public static final class ContainerId {
            public static final String NAME = "container_id";
        }

        public static final class Origin {
            public static final String NAME = "journey_origin";
            public static final int LENGTH = 255;
        }

        public static final class Destination {
            public static final String NAME = "journey_destination";
            public static final int LENGTH = 255;
        }

        public static final class StartDate {
            public static final String NAME = "journey_start_date";
        }

        public static final class EndDate {
            public static final String NAME = "journey_end_date";
        }

        public static final class Description {
            public static final String NAME = "journey_description";
            public static final int LENGTH = 255;
        }

        public static final class Type {
            public static final String NAME = "journey_type";
            public static final int LENGTH = 50;
        }

        public static final class State {
            public static final String NAME = "journey_state";
        }

        public static final class ExecutionType {
            public static final String NAME = "execution_type";
        }

        public static final class AssignmentId {
            public static final String NAME = "assignment_id";
        }

        public static final class AuxiliaryProviderId {
            public static final String NAME = "auxiliary_provider_id";
        }

        public static final class AuxiliaryTruckPlate {
            public static final String NAME = "auxiliary_truck_plate";
            public static final int LENGTH = 20;
        }

        public static final class AuxiliaryDriverName {
            public static final String NAME = "auxiliary_driver_name";
            public static final int LENGTH = 150;
        }

        public static final class AuxiliaryDriverLicense {
            public static final String NAME = "auxiliary_driver_license";
            public static final int LENGTH = 40;
        }

        public static final class AuxiliaryReason {
            public static final String NAME = "auxiliary_reason";
        }

        public static final class AgreedAmount {
            public static final String NAME = "agreed_amount";
            public static final int PRECISION = 15;
            public static final int SCALE = 2;
        }

        public static final class CreatedAt {
            public static final String NAME = "created_at";
        }
    }
}
