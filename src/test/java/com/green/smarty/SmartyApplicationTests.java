package com.green.smarty;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.green.smarty.dto.AnnounceDTO;
import com.green.smarty.mapper.AnnounceMapper;
import com.green.smarty.service.AnnounceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.green.smarty.mapper.UserReservationMapper;
import com.green.smarty.vo.CourtVO;
import com.green.smarty.vo.FacilityVO;
import com.green.smarty.vo.ReservationVO;
import com.green.smarty.vo.UserVO;

@SpringBootTest
class SmartyApplicationTests {
	@Autowired
	private UserReservationMapper reservationMapper;

	@Autowired
	private AnnounceMapper announceMapper;

	@Test
	public void insertFacility() {
		List<String> f_arr = Arrays.asList(new String[] { "수영장", "농구장", "축구장", "야구장", "체육관" });
		int cnt = 1;
		for (String i : f_arr) {
			String id = "fc_" + System.currentTimeMillis();
			FacilityVO vo = FacilityVO.builder()
					.facility_id(id)
					.facility_name(i)
					.open_time("0" + (3 + cnt) + ":00:00")
					.close_time(17 + cnt + ":00:00")
					.default_time(1 + cnt)
					.basic_fee((int) ((Math.round(Math.random() * 100000) / 1000) * 1000))
					.rate_adjustment(0.1f)
					.hot_time((int) (Math.random() * 3))
					.contact("010-1234-5678")
					.info("안내사항")
					.caution("주의사항")
					.court(true)
					.product(false)
					.facility_status(true)
					.facility_images(false)
					.build();
			int result = reservationMapper.insertFacility(vo);
			cnt++;
			System.out.println(result);
		}
	}

	@Test
	public void insertUser() {
		UserVO vo = UserVO.builder()
				.user_id("kaka")
				.user_name("kaka")
				.email("kaka@kaka")
				.password("1234")
				.phone("010-1234-4567")
				.address("경기도 성남시")
				.birthday(LocalDate.of(2024, 1, 23))
				.join_date(LocalDateTime.now())
				.login_date(LocalDate.now())
				.user_status(true)
				.level("silver")
				.build();
		int result = reservationMapper.insertUser(vo);
		System.out.println(result);
	}

	@Test
	public void insert_court() {
		String[] facility_id = { "fc_1731986897322",
				"fc_1731986897365",
				"fc_1731986897375",
				"fc_1731986897382",
				"fc_1731986897388"};
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < facility_id.length; j++) {
				CourtVO vo = CourtVO.builder()
						.court_id("C_00000" + (i < 2 ? j : j + facility_id.length))
						.court_name(i + "번코트")
						.facility_id(facility_id[j])
						.court_status(true)
						.build();
				int result = reservationMapper.insertCourt(vo);
				System.out.println(result);
			}
		}
	}


}
