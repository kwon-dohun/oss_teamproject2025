package com.gmbbd.checkMate.service;

import com.gmbbd.checkMate.model.EvaluationResult;

public interface LlmService {

    EvaluationResult evaluateRequirement(String requirementText, String documentText);

}
