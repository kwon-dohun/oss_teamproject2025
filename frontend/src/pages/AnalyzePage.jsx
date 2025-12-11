import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import { analyzeRequest } from "../api/checkMateApi.js";
import logo from "../assets/logo.png";
import "./AnalyzePage.css";

function AnalyzePage() {
    const { state } = useLocation();
    const navigate = useNavigate();

    const [activeStep, setActiveStep] = useState(0);
    const [completedStep, setCompletedStep] = useState(0);
    const [dotCount, setDotCount] = useState(1);

    const startedRef = useRef(false);

    const requirementsFile = state?.requirementsFile;
    const submissionFile = state?.submissionFile;

    const stepTexts = [
        "Analyzing requirements",
        "Analyzing submission document",
        "Compiling validation report"
    ];

    useEffect(() => {
        if (!requirementsFile || !submissionFile) {
            navigate("/");
            return;
        }

        if (startedRef.current) return;
        startedRef.current = true;

        const dotInterval = setInterval(() => {
            setDotCount(prev => (prev === 3 ? 1 : prev + 1));
        }, 450);

        const timers = [];
        timers.push(setTimeout(() => setActiveStep(1), 300));
        timers.push(setTimeout(() => {
            setCompletedStep(1);
            setActiveStep(2);
        }, 1500));
        timers.push(setTimeout(() => {
            setCompletedStep(2);
            setActiveStep(3);
        }, 2700));

        (async () => {
            try {
                const apiResult = await analyzeRequest(requirementsFile, submissionFile);

                let results = [];
                if (Array.isArray(apiResult)) results = apiResult;
                else if (apiResult?.details) results = apiResult.details;

                setCompletedStep(3);

                setTimeout(() => {
                    navigate("/result", {
                        state: { results, requirementsFile, submissionFile }
                    });
                }, 700);

            } catch (err) {
                console.error(err);
                alert("An error occurred during analysis.");
                navigate("/");
            }
        })();

        return () => {
            timers.forEach(clearTimeout);
            clearInterval(dotInterval);
        };
    }, []);

    return (
        <div className="analyze-root">

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

            <div className="analyze-container">

                <div className="spinner"></div>

                <h1 className="loading-title">Analyzing your documents...</h1>

                <div className="steps-wrapper">
                    {stepTexts.map((txt, i) => {
                        const step = i + 1;
                        const isActive = activeStep === step;
                        const isCompleted = completedStep >= step;

                        const animated =
                            isActive && !isCompleted
                                ? txt + ".".repeat(dotCount)
                                : txt;
                        return (
                            <div
                                key={i}
                                className={`step-box
                                    ${isActive ? "active" : ""}
                                    ${isCompleted ? "completed" : ""}
                                `}
                            >
                                {animated}

                                {isCompleted && (
                                    <div className="check-circle">✔</div>
                                )}
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}

export default AnalyzePage;
