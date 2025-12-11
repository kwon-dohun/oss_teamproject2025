package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;
import com.gmbbd.checkMate.model.Requirement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ValidationService validationService;
    private final ParseService parseService;
    private final RequirementService requirementService;
    private final LlmService llmService;

    public List<EvaluationResult> evaluate(MultipartFile requirements, MultipartFile submission) {

        validationService.validateFile(requirements);
        validationService.validateFile(submission);

        String requirementText = parseService.extractText(requirements);
        String submissionText = parseService.extractText(submission);

        List<Requirement> reqList = requirementService.extractRequirements(requirementText);

        List<EvaluationResult> results = new ArrayList<>();

        for (Requirement req : reqList) {
            EvaluationResult r = llmService.evaluateRequirement(req.getRawText(), submissionText);
            r.setRequirementId(req.getId());
            results.add(r);
        }

        return results;
    }
}
