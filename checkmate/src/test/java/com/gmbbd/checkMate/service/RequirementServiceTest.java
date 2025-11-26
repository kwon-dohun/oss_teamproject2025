package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.Requirement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RequirementService에 대한 단위 테스트.
 *  - 요구사항 텍스트를 넣었을 때
 *    우리가 기대한 개수만큼 Requirement가 잘 만들어지는지 확인한다.
 */
class RequirementServiceTest {

    @Test
    void extractRequirements_basicCase() {
        // given: 테스트용 요구사항 텍스트 (PDF에서 뽑혔다고 가정)
        String reqText = """
                1. 시스템 개요를 설명하시오.
                2. ERD 다이어그램을 포함하시오.
                3. 요구분석 방법론을 두 가지 이상 기술하시오.
                """;

        RequirementService service = new RequirementService();

        // when: 요구사항 리스트로 변환
        List<Requirement> list = service.extractRequirements(reqText);

        // then: 개수와 내용이 기대와 같은지 검증
        assertEquals(3, list.size(), "요구사항 개수가 3개여야 한다.");

        assertEquals(1, list.get(0).getId());
        assertTrue(list.get(0).getRawText().contains("시스템 개요"));

        assertEquals(2, list.get(1).getId());
        assertTrue(list.get(1).getRawText().contains("ERD"));

        assertEquals(3, list.get(2).getId());
        assertTrue(list.get(2).getRawText().contains("요구분석 방법론"));
    }

    @Test
    void extractRequirements_emptyText_returnsEmptyList() {
        // given
        String reqText = "   "; // 공백만 있는 경우

        RequirementService service = new RequirementService();

        // when
        List<Requirement> list = service.extractRequirements(reqText);

        // then
        assertNotNull(list);
        assertEquals(0, list.size(), "공백 텍스트이면 비어있는 리스트가 나와야 한다.");
    }
}