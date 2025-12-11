const BASE_URL = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

export async function analyzeRequest(requirementsFile, submissionFile) {
    const formData = new FormData();
    formData.append("requirements", requirementsFile);
    formData.append("submission", submissionFile);

    const response = await fetch(`${BASE_URL}/api/analyze`, {
        method: "POST",
        body: formData,
    });

    if (!response.ok) {
        const errText = await response.text();
        throw new Error(errText || "분석 요청 실패");
    }

    return response.json();
}

export async function downloadReport(requirementsFile, submissionFile) {
    const formData = new FormData();
    formData.append("requirements", requirementsFile);
    formData.append("submission", submissionFile);

    const response = await fetch(`${BASE_URL}/api/report`, {
        method: "POST",
        body: formData,
    });

    if (!response.ok) throw new Error("보고서 다운로드 실패");

    return response.blob();
}
