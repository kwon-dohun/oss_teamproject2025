# CheckMate

AI-powered requirement validation web service

CheckMate is a web application that analyzes a requirement document and a submitted project document, then automatically determines whether each requirement is Fulfilled, Partially Fulfilled, or Not Fulfilled—along with clear evidence extracted from the submission.

---

### What does this project do?

CheckMate analyzes two uploaded documents:
1. Requirement Specification
2. Submission Document (report, implementation description, documentation, etc.)

Using AI-based text comparison, the system detects which requirements are met and presents:
- Requirement status (Fulfilled / Partially Fulfilled / Not Fulfilled)
- Supporting or missing evidence
- Downloadable evaluation results as a .txt file
- A clean and simple web UI for easy interaction

This completely removes manual cross-checking and enables consistent, objective evaluation.

---

### Why is this project useful?

Many academic and team projects rely on manual checklist evaluation, which is:
- Time-consuming
- Inconsistent across evaluators
- Prone to oversight

CheckMate solves this by providing:
- Automated requirement verification
- Consistent evaluation logic
- Support for multiple document formats (PDF, DOCX, TXT)
- Clear evidence for each decision
- Fast and transparent feedback for teams and instructors

It is especially useful for:
- University team projects
- Open-source development assignments
- Document-based evaluations
- Self-checking before submitting official reports

---

### Project URL
https://oss-gmbbd.onrender.com

1. Usage
    1) Access the web UI
    2) Upload a requirement document
    3) Upload a submission document
    4) Receive automatic analysis
    5) Review requirement statuses and evidence
    6) Download the result file if needed
       
2. Usage Examples:
<img width="536" height="323" alt="image" src="https://github.com/user-attachments/assets/d2046f4e-2610-48fb-b28f-a9aae42aea80" />
   <img width="537" height="283" alt="image" src="https://github.com/user-attachments/assets/7ad52d34-7deb-4cc1-8e25-9783456cc216" />
   <img width="540" height="329" alt="image" src="https://github.com/user-attachments/assets/bfc6ed74-5b31-47d0-a6fc-1c141b4e1915" />



Everything works through a clean and minimal interface.

---

### Contributing & Feedback

- Use GitHub Issues & Discussions for bug reports, feature requests, and general questions.
- Refer to the source code (frontend + backend) for implementation details.
- Additional documentation will be added to the /docs directory.

Feel free to open an Issue anytime if you have ideas, improvements, or problems to report.

---

### Contribution Workflow

Contributions are welcome.
Please follow the project workflow for smooth collaboration:
- Discuss ideas or issues through Issues
- Submit changes via Pull Requests
- Follow the Code of Conduct and contribution guidelines
- Use the provided Issue / PR templates
- Ensure clarity, stability, and testing before requesting review
  
This ensures scalable, transparent development for all collaborators.

---

### What are the goals of the project?

1. Automate requirement validation to reduce manual work.
2. Provide objective and consistent evaluation results.
3. Support multiple document formats for practical use.
4. Deliver clear explanations and evidence for all decisions.
5. Build a maintainable, extendable open-source project.
6. Serve as a reference for learning proper OSS workflows.

## Third-Party Licenses

This Docker image includes **Pandoc**, which is licensed under the **GNU GPL**.
Pandoc is bundled as an external CLI tool and is not linked with the CheckMate codebase.
The CheckMate project itself remains under the **MIT License**.

Pandoc source code: https://github.com/jgm/pandoc  
Pandoc license (GPL): https://github.com/jgm/pandoc/blob/master/COPYING





