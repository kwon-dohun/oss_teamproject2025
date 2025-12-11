import {useState} from "react";
import {useNavigate} from "react-router-dom";
import logo from "../assets/logo.png";
import logo_main from "../assets/logo_main.png";
import "./UploadPage.css";

function UploadPage() {
    const navigate = useNavigate();

    const [reqFile, setReqFile] = useState(null);
    const [subFile, setSubFile] = useState(null);
    const [reqError, setReqError] = useState(false);
    const [subError, setSubError] = useState(false);
    const [reqDrag, setReqDrag] = useState(false);
    const [subDrag, setSubDrag] = useState(false);
    const [loading, setLoading] = useState(false);

    const allowed = ["pdf", "docx", "txt"];

    const validateFile = (file, type) => {
        const ext = file.name.split(".").pop().toLowerCase();
        const valid = allowed.includes(ext);

        if (!valid) {
            if (type === "req") setReqError(true);
            else setSubError(true);
            return false;
        }

        if (type === "req") {
            setReqFile(file);
            setReqError(false);
        } else {
            setSubFile(file);
            setSubError(false);
        }
        return true;
    };

    const handleAnalyze = () => {
        if (!reqFile || !subFile) return;

        setLoading(true);
        setTimeout(() => {
            setLoading(false);
            navigate("/analyze", {
                state: {
                    requirementsFile: reqFile,
                    submissionFile: subFile
                }
            });
        }, 300);
    };

    return (
        <>
            {loading && (
                <div className="loading-overlay">
                    <div className="loading-box">
                        <div className="spinner"></div>
                        <p className="loading-text">Setting up the analysis...</p>
                    </div>
                </div>
            )}

            <nav className="navbar">
                <div
                    className="nav-left"
                    onClick={() => navigate("/")}
                    style={{cursor: "pointer"}}
                >
                    <img
                        src={logo}
                        alt="logo"
                        style={{height: "110px"}}
                    />
                </div>
            </nav>

            <div className="hero-section">
                <div className="hero-title"><img
                    src={logo_main}
                    alt="logo_main"
                    style={{height: "140px"}}
                />
                </div>
                <p className="hero-subtitle">
                    We analyze your document by comparing it with the requirements to determine its compliance.
                </p>
                <div className="hero-full-divider"/>

                <div className="step-guide">
                    <div className="step-item">
                        <span className="step-badge">Step 1</span>
                        <span className="step-text">
                            Upload the requirements and submission documents.
                        </span>
                    </div>
                    <div className="step-item">
                        <span className="step-badge">Step 2</span>
                        <span className="step-text">
                            AI analyzes and evaluates the documents.
                        </span>
                    </div>
                    <div className="step-item">
                        <span className="step-badge">Step 3</span>
                        <span className="step-text">
                            Review the compliance score and detailed results.
                        </span>
                    </div>
                </div>

            </div>

            <div className="upload-wrapper">

                <div className="upload-container">

                    <div className="upload-card">
                        <div className="card-title">📘 Requirement Document</div>

                        <div
                            className={`dropzone 
                                ${reqError ? "dropzone-error" : ""} 
                                ${reqDrag ? "drag-active" : ""}`}
                            onDragOver={(e) => e.preventDefault()}
                            onDragEnter={() => setReqDrag(true)}
                            onDragLeave={() => setReqDrag(false)}
                            onDrop={(e) => {
                                e.preventDefault();
                                setReqDrag(false);
                                validateFile(e.dataTransfer.files[0], "req");
                            }}
                        >
                            <input
                                type="file"
                                accept=".pdf,.docx,.txt"
                                className="file-input"
                                onChange={(e) => validateFile(e.target.files[0], "req")}
                            />

                            {reqFile ? (
                                <span className="file-name">{reqFile.name}</span>
                            ) : (
                                <span className="placeholder">Drag a file here or click to upload</span>
                            )}
                        </div>

                        {reqError && (
                            <div className="error-text">
                                Unsupported file format.
                            </div>
                        )}

                        <div className="file-hint">
                            • Supported formats: PDF, DOCX, TXT
                        </div>
                    </div>

                    {/* SUBMISSION */}
                    <div className="upload-card">
                        <div className="card-title">📗 Submission Document</div>

                        <div
                            className={`dropzone 
                                ${subError ? "dropzone-error" : ""}
                                ${subDrag ? "drag-active" : ""}`}
                            onDragOver={(e) => e.preventDefault()}
                            onDragEnter={() => setSubDrag(true)}
                            onDragLeave={() => setSubDrag(false)}
                            onDrop={(e) => {
                                e.preventDefault();
                                setSubDrag(false);
                                validateFile(e.dataTransfer.files[0], "sub");
                            }}
                        >
                            <input
                                type="file"
                                accept=".pdf,.docx,.txt"
                                className="file-input"
                                onChange={(e) => validateFile(e.target.files[0], "sub")}
                            />

                            {subFile ? (
                                <span className="file-name">{subFile.name}</span>
                            ) : (
                                <span className="placeholder">Drag a file here or click to upload</span>
                            )}
                        </div>

                        {subError && (
                            <div className="error-text">
                                Unsupported file format.
                            </div>
                        )}

                        <div className="file-hint">
                            • Supported formats: PDF, DOCX, TXT
                        </div>
                    </div>
                </div>

                {/* BUTTON */}
                <button
                    className="analyze-button"
                    disabled={!reqFile || !subFile || loading}
                    onClick={handleAnalyze}
                >
                    Analyze
                </button>

            </div>

            {/* FOOTER */}
            <footer className="footer">
                <div className="footer-inner">
                    <div className="footer-col">
                        <h4>CheckMate</h4>
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
        </>
    );
}

export default UploadPage;
