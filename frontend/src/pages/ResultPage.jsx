import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { downloadReport } from "../api/checkMateApi.js";
import "./ResultPage.css";
import logo from "../assets/logo.png";

function ResultPage() {
    const { state } = useLocation();
    const navigate = useNavigate();
    const [selected, setSelected] = useState(null);

    const convertStatus = (status) => {
        switch (status) {
            case "FULFILLED": return "Fullfilled";
            case "PARTIAL": return "Partially fullfilled";
            case "NOT_FULFILLED": return "Not fullfilled";
            default: return status;
        }
    };

    if (!state || !Array.isArray(state.results) || state.results.length === 0) {
        return (
            <div className="result-root">
                <nav className="navbar">
                    <div className="nav-left">
                        CheckMate
                    </div>
                </nav>

                <div className="result-container">
                    <div className="empty-card">
                        <h2>Unable to load the result data.</h2>
                        <p>Please return to the upload page and run the analysis again.</p>

                        <button className="restart-btn primary" onClick={() => navigate("/")}>
                            Back to Upload Page
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    const { results, requirementsFile, submissionFile } = state;

    const fulfilled = results.filter(r => r.status === "FULFILLED").length;
    const partial = results.filter(r => r.status === "PARTIAL").length;
    const notFulfilled = results.filter(r => r.status === "NOT_FULFILLED").length;

    const total = results.length;
    const finalScore =
        total > 0 ? ((fulfilled + partial * 0.5) / total) * 100 : 0;

    const handleDownload = async () => {
        try {
            const blob = await downloadReport(requirementsFile, submissionFile);
            const url = URL.createObjectURL(blob);

            const a = document.createElement("a");
            a.href = url;
            a.download = "CheckMate_Report.txt";
            a.click();
            URL.revokeObjectURL(url);
        } catch {
            alert("An error occurred while downloading the report.");
        }
    };

    const openDetail = (r) => {
        const evidence =
            r.evidence?.trim()
                ? r.evidence
                : r.reason?.trim()
                    ? r.reason
                    : "No evidence available";

        setSelected({ ...r, evidence });
    };

    return (
        <div className="result-root">
            {/* NAV */}
            <nav className="navbar">
                <div
                    className="nav-left"
                    onClick={() => navigate("/")}
                    style={{ cursor: "pointer" }}
                >
                    <img
                        src={logo}
                        alt="logo"
                        style={{ height: "110px" }}
                    />
                </div>
            </nav>

            <div className="result-container">

                <section className="result-hero">
                    <h1 className="result-title">Analysis Summary</h1>
                    <p className="result-subtitle">Check the compliance results and supporting evidence.</p>
                    <div className="result-divider"></div>
                </section>

                <section className="summary-card">
                    <div className="summary-left">
                        <div className="summary-label">Final Score</div>
                        <div className="summary-score">
                            {finalScore.toFixed(1)}
                            <span className="summary-score-unit">pts</span>
                        </div>
                        <div className="summary-caption">(1 point for Fulfilled, 0.5 points for Partially Fulfilled)</div>
                    </div>

                    <div className="summary-right">
                        <div className="summary-stat-row fulfilled">
                            <span className="dot"></span>
                            <span>Fulfilled</span>
                            <strong>{fulfilled}</strong>
                        </div>
                        <div className="summary-stat-row partial">
                            <span className="dot"></span>
                            <span>Partially Fulfilled</span>
                            <strong>{partial}</strong>
                        </div>
                        <div className="summary-stat-row not">
                            <span className="dot"></span>
                            <span>Not fulfilled</span>
                            <strong>{notFulfilled}</strong>
                        </div>

                        <button className="download-btn" onClick={handleDownload}>
                            Download Report
                        </button>
                    </div>
                </section>

                <section className="detail-header">
                    <h2 className="detail-title">Detailed Results by Requirement</h2>
                    <p className="detail-subtitle">
                        Click an item to view the supporting evidence.
                    </p>
                </section>

                <section className="result-list">
                    {results.map((r, idx) => (
                        <div
                            key={idx}
                            className="result-item"
                            onClick={() => openDetail(r)}
                        >
                            <div className="result-main">
                                <b className="requirement-text">{r.requirementText}</b>
                                <span className={`status-badge status-${r.status}`}>
                                    {convertStatus(r.status)}
                                </span>
                            </div>

                            <div className="result-preview">
                                {(r.evidence || r.reason || "No evidence available").slice(0, 90)}
                                {(r.evidence?.length > 90 || r.reason?.length > 90) ? " ..." : ""}
                            </div>
                        </div>
                    ))}
                </section>

                {selected && (
                    <div className="modal-bg" onClick={() => setSelected(null)}>
                        <div className="modal-box" onClick={(e) => e.stopPropagation()}>
                            <h2 className="modal-title">Evidence Details</h2>

                            <div className="modal-section">
                                <b>Requirement</b>
                                <p>{selected.requirementText}</p>
                            </div>

                            <div className="modal-section">
                                <b>Decision</b>
                                <p>{convertStatus(selected.status)}</p>
                            </div>

                            <div className="modal-section">
                                <b>Evidence</b>
                                <p>{selected.evidence}</p>
                            </div>

                            <button className="modal-close" onClick={() => setSelected(null)}>
                                Close
                            </button>
                        </div>
                    </div>
                )}

                <button className="restart-btn" onClick={() => navigate("/")}>
                    Analyze Again
                </button>
            </div>

            {/* FOOTER */}
            <footer className="footer">
                <div className="footer-inner">
                    <div className="footer-col">
                        <h4>
                            CheckMate
                        </h4>
                        <p>AI-powered requirement validation service</p>
                    </div>
                    <div className="footer-col">
                        <h4>Team</h4>
                        <p>KwonDohun · KimGunwoo · OhKyounghun · LimDonghyun</p>
                    </div>
                    <div className="footer-col">
                        <h4>Contact</h4>
                        <p>ph. 010-6220-8271</p>
                    </div>
                </div>
            </footer>
        </div>
    );
}

export default ResultPage;
