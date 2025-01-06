package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourtVO {
    private String court_id;
    private String facility_id;
    private String court_name;
    private boolean court_status;

    public void changeName(String court_name) {this.court_name = court_name;}
    public void changeCourtStatus(Boolean court_status) {this.court_status = court_status;}
}
