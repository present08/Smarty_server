package com.green.smarty.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassVO {
    private String class_id;
    private String facility_id;
    private String class_name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate start_date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end_date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime start_time;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime end_time;

    private int price;
    private int class_size;

    public void changeName(String class_name) {this.class_name = class_name;}
    public void changeStartDate(LocalDate start_date) {this.start_date = start_date;}
    public void changeEndDate(LocalDate end_date) {this.end_date = end_date;}
    public void changeStartTime(LocalTime start_time) {this.start_time = start_time;}
    public void changeEndTime(LocalTime end_time) {this.end_time = end_time;}
    public void changePrice(int price) {this.price = price;}
    public void changeSize(int class_size) {this.class_size = class_size;}

}
