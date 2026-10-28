package com.manager.truck.truck.domain;

public final class Constants {

    private Constants() {
    }

    public static final class PersonTable {
        public static final String NAME = "person";

        public static final class Id {
            public static final String NAME = "person_id";
        }

        public static final class FirstName {
            public static final String NAME = "first_name";
            public static final int LENGTH = 100;
        }

        public static final class LastName {
            public static final String NAME = "last_name";
            public static final int LENGTH = 100;
        }

        public static final class MaternalLastName {
            public static final String NAME = "maternal_last_name";
            public static final int LENGTH = 100;
        }

        public static final class Email {
            public static final String NAME = "email";
            public static final int LENGTH = 255;
        }

        public static final class Phone {
            public static final String NAME = "phone";
            public static final int LENGTH = 25;
        }

        public static final class DocumentNumber {
            public static final String NAME = "document_number";
            public static final int LENGTH = 30;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class UserTable {
        public static final String NAME = "user_table";

        public static final class Id {
            public static final String NAME = "user_id";
        }

        public static final class PersonId {
            public static final String NAME = "person_id";
        }

        public static final class PasswordHash {
            public static final String NAME = "password_hash";
            public static final int LENGTH = 255;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
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

        public static final class Description {
            public static final String NAME = "role_description";
            public static final int LENGTH = 255;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class UserRoleTable {
        public static final String NAME = "user_role";

        public static final class UserId {
            public static final String NAME = "user_id";
        }

        public static final class RoleId {
            public static final String NAME = "role_id";
        }

        public static final class GrantedAt {
            public static final String NAME = "granted_at";
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class PermissionTable {
        public static final String NAME = "permission_table";

        public static final class Id {
            public static final String NAME = "permission_id";
        }

        public static final class Code {
            public static final String NAME = "permission_code";
            public static final int LENGTH = 80;
        }

        public static final class Description {
            public static final String NAME = "description";
            public static final int LENGTH = 255;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class RolePermissionTable {
        public static final String NAME = "role_permission";

        public static final class RoleId {
            public static final String NAME = "role_id";
        }

        public static final class PermissionId {
            public static final String NAME = "permission_id";
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class PartnerTable {
        public static final String NAME = "partner_table";

        public static final class Id {
            public static final String NAME = "partner_id";
        }

        public static final class Type {
            public static final String NAME = "partner_type";
            public static final int LENGTH = 8;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class DriverTable {
        public static final String NAME = "driver_table";

        public static final class Id {
            public static final String NAME = "driver_id";
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

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class TruckTable {
        public static final String NAME = "truck_table";

        public static final class Id {
            public static final String NAME = "truck_id";
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

        public static final class EmptyWeight {
            public static final String NAME = "truck_empty_weight_kg";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class TrailerEmptyWeight {
            public static final String NAME = "trailer_empty_weight_kg";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class MaxGrossWeight {
            public static final String NAME = "max_gross_weight_kg";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class Type {
            public static final String NAME = "truck_type";
            public static final int LENGTH = 50;
        }

        public static final class ChassisPlate {
            public static final String NAME = "chassis_plate";
            public static final int LENGTH = 20;
        }

        public static final class InsuranceExpiration {
            public static final String NAME = "insurance_expiration";
        }

        public static final class TechnicalInspectionExpiration {
            public static final String NAME = "technical_inspection_expiration";
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class TruckDriverAssignmentTable {
        public static final String NAME = "truck_driver_assignment";

        public static final class Id {
            public static final String NAME = "assignment_id";
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

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class ClientTable {
        public static final String NAME = "client";

        public static final class Id {
            public static final String NAME = "client_id";
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

        public static final class TaxId {
            public static final String NAME = "tax_id";
            public static final int LENGTH = 30;
        }

        public static final class ContactPerson {
            public static final String NAME = "contact_person";
            public static final int LENGTH = 150;
        }

        public static final class ContactPhone {
            public static final String NAME = "contact_phone";
            public static final int LENGTH = 25;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class ContractTable {
        public static final String NAME = "contract_table";

        public static final class Id {
            public static final String NAME = "contract_id";
        }

        public static final class ClientId {
            public static final String NAME = "client_id";
        }

        public static final class ContractCode {
            public static final String NAME = "contract_code";
            public static final int LENGTH = 40;
        }

        public static final class OperationType {
            public static final String NAME = "operation_type";
            public static final int LENGTH = 12;
        }

        public static final class OriginLocation {
            public static final String NAME = "origin_location";
            public static final int LENGTH = 255;
        }

        public static final class DestinationLocation {
            public static final String NAME = "destination_location";
            public static final int LENGTH = 255;
        }

        public static final class ExpectedStartDate {
            public static final String NAME = "expected_start_date";
        }

        public static final class ExpectedDeliveryDate {
            public static final String NAME = "expected_delivery_date";
        }

        public static final class ActualDeliveryDate {
            public static final String NAME = "actual_delivery_date";
        }

        public static final class ReferenceDocument {
            public static final String NAME = "reference_document";
            public static final int LENGTH = 80;
        }

        public static final class DeclaredTotalWeightKg {
            public static final String NAME = "declared_total_weight_kg";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class ExpectedContainerCount {
            public static final String NAME = "expected_container_count";
        }

        public static final class Observations {
            public static final String NAME = "observations";
        }

        public static final class ContractAmount {
            public static final String NAME = "contract_amount";
            public static final int PRECISION = 15;
            public static final int SCALE = 2;
        }

        public static final class ContractDate {
            public static final String NAME = "contract_date";
        }

        public static final class ContractDescription {
            public static final String NAME = "contract_description";
            public static final int LENGTH = 255;
        }

        public static final class ContractType {
            public static final String NAME = "contract_type";
            public static final int LENGTH = 50;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class ChargeTable {
        public static final String NAME = "charge_table";

        public static final class Id {
            public static final String NAME = "charge_id";
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

        public static final class CargoReference {
            public static final String NAME = "cargo_reference";
            public static final int LENGTH = 80;
        }

        public static final class GoodsDescription {
            public static final String NAME = "goods_description";
        }

        public static final class HazardousGoods {
            public static final String NAME = "hazardous_goods";
        }

        public static final class HandlingInstructions {
            public static final String NAME = "handling_instructions";
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class ContainerTable {
        public static final String NAME = "container_table";

        public static final class Id {
            public static final String NAME = "container_id";
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

        public static final class IsoSize {
            public static final String NAME = "iso_size";
            public static final int LENGTH = 10;
        }

        public static final class ContainerFormat {
            public static final String NAME = "container_format";
            public static final int LENGTH = 25;
        }

        public static final class SealNumber {
            public static final String NAME = "seal_number";
            public static final int LENGTH = 50;
        }

        public static final class GrossWeightKg {
            public static final String NAME = "gross_weight_kg";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class TareWeightKg {
            public static final String NAME = "tare_weight_kg";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class CargoWeightKg {
            public static final String NAME = "cargo_weight_kg";
            public static final int PRECISION = 12;
            public static final int SCALE = 3;
        }

        public static final class ShippingLine {
            public static final String NAME = "shipping_line";
            public static final int LENGTH = 120;
        }

        public static final class CustomsReference {
            public static final String NAME = "customs_reference";
            public static final int LENGTH = 80;
        }

        public static final class TemperatureC {
            public static final String NAME = "temperature_c";
            public static final int PRECISION = 5;
            public static final int SCALE = 2;
        }

        public static final class ContainerDate {
            public static final String NAME = "container_date";
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class FreightTable {
        public static final String NAME = "freight_table";

        public static final class Id {
            public static final String NAME = "freight_id";
        }

        public static final class ContractId {
            public static final String NAME = "contract_id";
        }

        public static final class ContainerId {
            public static final String NAME = "container_id";
        }

        public static final class TruckId {
            public static final String NAME = "truck_id";
        }

        public static final class AuxiliaryProviderId {
            public static final String NAME = "auxiliary_provider_id";
        }

        public static final class AuxiliaryTruckPlate {
            public static final String NAME = "auxiliary_truck_plate";
            public static final int LENGTH = 20;
        }

        public static final class FreightDescription {
            public static final String NAME = "freight_description";
            public static final int LENGTH = 255;
        }

        public static final class FreightCode {
            public static final String NAME = "freight_code";
            public static final int LENGTH = 40;
        }

        public static final class ScheduledDeparture {
            public static final String NAME = "scheduled_departure";
        }

        public static final class ScheduledArrival {
            public static final String NAME = "scheduled_arrival";
        }

        public static final class ActualDeparture {
            public static final String NAME = "actual_departure";
        }

        public static final class ActualArrival {
            public static final String NAME = "actual_arrival";
        }

        public static final class DeliveryNotes {
            public static final String NAME = "delivery_notes";
        }

        public static final class FreightAmount {
            public static final String NAME = "freight_amount";
            public static final int PRECISION = 15;
            public static final int SCALE = 2;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class AuxiliaryProviderTable {
        public static final String NAME = "auxiliary_provider";

        public static final class Id {
            public static final String NAME = "provider_id";
        }

        public static final class Kind {
            public static final String NAME = "provider_kind";
            public static final int LENGTH = 11;
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

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }

    public static final class JourneyTable {
        public static final String NAME = "journey_table";

        public static final class Id {
            public static final String NAME = "journey_id";
        }

        public static final class FreightId {
            public static final String NAME = "freight_id";
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

        public static final class PlannedStartDate {
            public static final String NAME = "planned_start_date";
        }

        public static final class PlannedEndDate {
            public static final String NAME = "planned_end_date";
        }

        public static final class DeliveryConfirmedAt {
            public static final String NAME = "delivery_confirmed_at";
        }

        public static final class RecipientName {
            public static final String NAME = "recipient_name";
            public static final int LENGTH = 150;
        }

        public static final class ProofOfDeliveryReference {
            public static final String NAME = "proof_of_delivery_reference";
            public static final int LENGTH = 255;
        }

        public static final class Observations {
            public static final String NAME = "observations";
        }

        public static final class AssignmentId {
            public static final String NAME = "assignment_id";
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
            public static final int LENGTH = 16;
        }

        public static final class AgreedAmount {
            public static final String NAME = "agreed_amount";
            public static final int PRECISION = 15;
            public static final int SCALE = 2;
        }

        public static final class DateCreate {
            public static final String NAME = "date_create";
        }

        public static final class DateUpdate {
            public static final String NAME = "date_update";
        }

        public static final class IsDelete {
            public static final String NAME = "is_delete";
        }

        public static final class State {
            public static final String NAME = "state";
            public static final int LENGTH = 20;
        }
    }
}
