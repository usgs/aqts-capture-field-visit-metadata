insert into field_visit_readings_by_loc (
  json_data_id,
  field_visit_identifier,
  control_condition,
  approval_level,
  approval_level_description,
  unit,
  display_value,
  uncertainty_type,
  quantitative_uncertainty,
  qualitative_uncertainty,
  parameter,
  monitoring_method,
  subLocation_identifier,
  manufacturer,
  model,
  serial_number,
  field_visit_time,
  field_visit_comments,
  publish,
  grade_code,
  qualifiers,
  reading_type,
  reference_point_unique_id,
  use_location_datum_as_reference,
  partition_number,
  location_identifier
)
select
  b.json_data_id,
  jsonb_extract_path_text(b.reading, 'FieldVisitIdentifier') field_visit_identifier,
  jsonb_extract_path_text(b.reading, 'ControlCondition') control_condition,
  jsonb_extract_path_text(b.approval, 'ApprovalLevel') approval_level,
  jsonb_extract_path_text(b.approval, 'LevelDescription') approval_level_description,
  jsonb_extract_path_text(b.reading, 'Value', 'Unit') unit,
  jsonb_extract_path_text(b.reading, 'Value', 'Display') display_value,
  jsonb_extract_path_text(b.uncertainty, 'UncertaintyType') uncertainty_type,
  jsonb_extract_path_text(b.uncertainty, 'QuantitativeUncertainty', 'Display') quantitative_uncertainty,
  jsonb_extract_path_text(b.uncertainty, 'QualitativeUncertainty') qualitative_uncertainty,
  jsonb_extract_path_text(b.reading, 'Parameter') parameter,
  jsonb_extract_path_text(b.reading, 'MonitoringMethod') monitoring_method,
  jsonb_extract_path_text(b.reading, 'SubLocationIdentifier') subLocation_identifier,
  jsonb_extract_path_text(b.reading, 'Manufacturer') manufacturer,
  jsonb_extract_path_text(b.reading, 'Model') model,
  jsonb_extract_path_text(b.reading, 'SerialNumber') serial_number,
  adjust_timestamp(jsonb_extract_path_text(b.reading, 'Time')) field_visit_time,
  jsonb_extract_path_text(b.reading, 'Comments') field_visit_comments,
  jsonb_extract_path_text(b.reading, 'Publish') publish,
  jsonb_extract_path_text(b.reading, 'GradeCode') grade_code,
  jsonb_extract_path_text(b.reading, 'Qualifiers')::text qualifiers,
  jsonb_extract_path_text(b.reading, 'reading_type') reading_type,
  jsonb_extract_path_text(b.reading, 'ReferencePointUniqueId') reference_point_unique_id,
  jsonb_extract_path_text(b.reading, 'UseLocationDatumAsReference') use_location_datum_as_reference,
  b.partition_number,
  b.location_identifier
  from (
    select
      a.json_data_id,
      a.approval,
      field_visit_readings reading,      
      a.uncertainty,
      a.partition_number,
      a.location_identifier
      from (
        select
          jd.json_data_id,
          jsonb_array_elements(jsonb_extract_path(jd.json_content, 'FieldVisitReadings')) as field_visit_readings,
          jsonb_extract_path(jsonb_array_elements(jsonb_extract_path(jd.json_content, 'FieldVisitReadings')), 'Approval') as approval,
          jsonb_extract_path(jsonb_array_elements(jsonb_extract_path(jd.json_content, 'FieldVisitReadings')), 'Uncertainty') as uncertainty,
          jd.partition_number,
          jsonb_extract_path_text(jd.parameters, 'locationIdentifier') location_identifier
      from json_data jd
      where json_data_id = ?
      and partition_number = ?
    ) a
) b;
