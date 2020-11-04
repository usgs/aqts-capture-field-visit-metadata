insert into datum_converted_values (
  json_data_id,
  field_visit_identifier,
  field_visit_time,
  target_datum,
  unit,
  display_value,
  partition_number
)
select
  b.json_data_id,
  jsonb_extract_path_text(b.reading, 'FieldVisitIdentifier') field_visit_identifier,
  adjust_timestamp(jsonb_extract_path_text(b.reading, 'Time')) field_visit_time,
  jsonb_extract_path_text(b.datum_converted_value, 'TargetDatum') target_datum,
  jsonb_extract_path_text(b.datum_converted_value, 'Unit') unit,
  jsonb_extract_path_text(b.datum_converted_value, 'Display') display_value,
  b.partition_number
  from (
    select
      a.json_data_id,
      a.field_visit_readings reading,
      jsonb_array_elements(
              jsonb_extract_path(a.field_visit_readings, 'DatumConvertedValues')) datum_converted_value,
      a.partition_number
      from (
        select
          jd.json_data_id,
          jsonb_array_elements(jsonb_extract_path(jsonb_extract_path(jd.json_content, 'FieldVisitReadingsResponse'), 'FieldVisitReadings')) as field_visit_readings,
          jd.partition_number
      from json_data jd
      where json_data_id = ?
      and partition_number = ?
    ) a
  ) b;