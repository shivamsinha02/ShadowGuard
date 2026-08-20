import { useEffect, useState, useRef } from "react";
import "./App.css";

const API_BASE_URL = "https://shadowguard-sbsv.onrender.com";

function App() {

  const [stats, setStats] = useState({
    totalApis: 0,
    shadowApis: 0,
    highRisk: 0,
    criticalRisk: 0
  });

  const [shadowApis, setShadowApis] = useState([]);
  const [allApis, setAllApis] = useState([]);

  const [filter, setFilter] = useState("ALL");

  const [selectedApi, setSelectedApi] = useState(null);
  const [aiAnalysis, setAiAnalysis] = useState("");
  const [loadingAI, setLoadingAI] = useState(false);

  const [scanning, setScanning] = useState(false);

  // Reference for AI Analysis section
  const aiAnalysisRef = useRef(null);


  // =========================
  // FETCH DASHBOARD DATA
  // =========================

  const fetchDashboardData = () => {

    // Fetch statistics
    fetch(`${API_BASE_URL}/api/dashboard/stats`)
      .then(response => response.json())
      .then(data => {
        setStats(data);
      })
      .catch(error => {
        console.error("Error fetching stats:", error);
      });


    // Fetch shadow APIs
    fetch(`${API_BASE_URL}/api/dashboard/shadow`)
      .then(response => response.json())
      .then(data => {
        setShadowApis(data);
      })
      .catch(error => {
        console.error("Error fetching shadow APIs:", error);
      });


    // Fetch all APIs
    fetch(`${API_BASE_URL}/api/apis`)
      .then(response => response.json())
      .then(data => {
        setAllApis(data);
      })
      .catch(error => {
        console.error("Error fetching all APIs:", error);
      });
  };


  // =========================
  // INITIAL LOAD
  // =========================

  useEffect(() => {

    fetchDashboardData();

  }, []);


  // =========================
// AUTO SCROLL TO AI ANALYSIS
// =========================

useEffect(() => {

  if (!selectedApi || loadingAI) {
    return;
  }

  const scrollToAI = () => {

    if (!aiAnalysisRef.current) {
      return;
    }

    const element = aiAnalysisRef.current;

    const elementPosition =
      element.getBoundingClientRect().top + window.scrollY;

    const offset = 40;

    window.scrollTo({
      top: elementPosition - offset,
      behavior: "smooth"
    });
  };

  // Wait until React has completely rendered AI analysis
  const timer = setTimeout(() => {
    requestAnimationFrame(() => {
      scrollToAI();
    });
  }, 300);

  return () => clearTimeout(timer);

}, [selectedApi, loadingAI, aiAnalysis]);


  // =========================
  // SCAN APIs
  // =========================

  const scanApis = () => {

    setScanning(true);

    fetch(`${API_BASE_URL}/api/discovery/scan`)
      .then(response => {

        if (!response.ok) {
          throw new Error("API scan failed");
        }

        return response.json();
      })
      .then(() => {

        // Refresh complete dashboard
        fetchDashboardData();

      })
      .catch(error => {

        console.error("API scan error:", error);

      })
      .finally(() => {

        setScanning(false);

      });
  };


// =========================
// AI SECURITY ANALYSIS
// =========================

const analyzeWithAI = (api) => {

  setSelectedApi(api);
  setAiAnalysis("");
  setLoadingAI(true);

  fetch(`${API_BASE_URL}/api/ai/analyze/${api.id}`)
    .then(response => {

      if (!response.ok) {
        throw new Error("AI analysis failed");
      }

      return response.text();
    })
    .then(data => {

      setAiAnalysis(data);

    })
    .catch(error => {

      console.error("AI analysis error:", error);

      setAiAnalysis(
        "AI analysis is temporarily unavailable. Please try again."
      );

    })
    .finally(() => {

      setLoadingAI(false);

    });
};


  // =========================
  // FILTER APIs
  // =========================

  const filteredApis = allApis.filter((api) => {

    if (filter === "ALL") {
      return true;
    }

    if (filter === "SHADOW") {
      return api.source === "DISCOVERED";
    }

    if (filter === "HIGH") {
      return api.riskLevel === "HIGH";
    }

    if (filter === "CRITICAL") {
      return api.riskLevel === "CRITICAL";
    }

    if (filter === "DOCUMENTED") {
      return api.source === "DOCUMENTED";
    }

    if (filter === "DISCOVERED") {
      return api.source === "DISCOVERED";
    }

    return true;
  });


  // =========================
  // PARSE AI RESPONSE
  // =========================

  const parseAIAnalysis = (text) => {

    if (!text) {

      return {
        summary: "",
        impact: "",
        recommendation: ""
      };
    }

    const summaryMatch = text.match(
      /SUMMARY:\s*([\s\S]*?)(?=IMPACT:|RECOMMENDATION:|$)/i
    );

    const impactMatch = text.match(
      /IMPACT:\s*([\s\S]*?)(?=RECOMMENDATION:|$)/i
    );

    const recommendationMatch = text.match(
      /RECOMMENDATION:\s*([\s\S]*?)$/i
    );

    return {

      summary: summaryMatch
        ? summaryMatch[1].trim()
        : text,

      impact: impactMatch
        ? impactMatch[1].trim()
        : "",

      recommendation: recommendationMatch
        ? recommendationMatch[1].trim()
        : ""
    };
  };


  // =========================
  // UI
  // =========================

  return (

    <div className="dashboard">


      {/* HEADER */}

      <header className="header">

        <h1>ShadowGuard</h1>

        <p>
          AI-Powered Shadow API Risk Analyzer
        </p>

        <button
          className="scan-button"
          onClick={scanApis}
          disabled={scanning}
        >

          {scanning
            ? "Scanning..."
            : "🔍 Scan APIs"}

        </button>

      </header>


      {/* STATISTICS */}

      <section className="stats">

        <div className="card">

          <h3>Total APIs</h3>

          <p>
            {stats.totalApis}
          </p>

        </div>


        <div className="card">

          <h3>Shadow APIs</h3>

          <p>
            {stats.shadowApis}
          </p>

        </div>


        <div className="card">

          <h3>High Risk</h3>

          <p>
            {stats.highRisk}
          </p>

        </div>


        <div className="card">

          <h3>Critical</h3>

          <p>
            {stats.criticalRisk}
          </p>

        </div>

      </section>


      {/* API SECTION */}

      <section className="shadow-section">

        <h2>
          Shadow APIs
        </h2>


        {/* FILTERS */}

        <div className="filters">

          <button
            onClick={() => setFilter("ALL")}
          >
            All
          </button>

          <button
            onClick={() => setFilter("SHADOW")}
          >
            Shadow
          </button>

          <button
            onClick={() => setFilter("HIGH")}
          >
            High
          </button>

          <button
            onClick={() => setFilter("CRITICAL")}
          >
            Critical
          </button>

          <button
            onClick={() => setFilter("DOCUMENTED")}
          >
            Documented
          </button>

          <button
            onClick={() => setFilter("DISCOVERED")}
          >
            Discovered
          </button>

        </div>


        {/* API LIST */}

        <div className="api-list">

          {filteredApis.length === 0 ? (

            <p className="empty-message">
              No APIs found for this filter.
            </p>

          ) : (

            filteredApis.map((api) => (

              <div
                className="api-row"
                key={api.id}
                onClick={() => analyzeWithAI(api)}
              >

                <span>
                  {api.endpoint}
                </span>


                <span
                  className={
                    api.riskLevel === "CRITICAL"
                      ? "critical"
                      : api.riskLevel === "HIGH"
                        ? "high"
                        : "low"
                  }
                >

                  {api.riskLevel || "N/A"}

                </span>

              </div>

            ))

          )}

        </div>


        {/* AI ANALYSIS */}

        {selectedApi && (

          <section
            className="ai-section"
            ref={aiAnalysisRef}
          >

            <h2>
              AI Security Analysis
            </h2>


            <div className="selected-api">

              <strong>
                {selectedApi.endpoint}
              </strong>

              <span>
                {selectedApi.riskLevel || "N/A"}
              </span>

            </div>


            {loadingAI ? (

              <div className="ai-loading">

                <div className="loading-dot"></div>

                <div>

                  <strong>
                    AI is analyzing this API...
                  </strong>

                  <p>
                    Checking security risks and generating recommendations.
                  </p>

                </div>

              </div>

            ) : (

              (() => {

                const analysis =
                  parseAIAnalysis(aiAnalysis);

                return (

                  <div className="ai-result">


                    {/* SUMMARY */}

                    <div className="analysis-card summary-card">

                      <div className="analysis-title">

                        <span>🔍</span>

                        <h3>
                          Summary
                        </h3>

                      </div>

                      <p>
                        {analysis.summary}
                      </p>

                    </div>


                    {/* IMPACT */}

                    <div className="analysis-card impact-card">

                      <div className="analysis-title">

                        <span>⚠️</span>

                        <h3>
                          Impact
                        </h3>

                      </div>

                      <p>
                        {analysis.impact ||
                          "No specific impact provided."}
                      </p>

                    </div>


                    {/* RECOMMENDATION */}

                    <div className="analysis-card recommendation-card">

                      <div className="analysis-title">

                        <span>🛡️</span>

                        <h3>
                          Recommendation
                        </h3>

                      </div>

                      <p>

                        {analysis.recommendation ||
                          "No specific recommendation provided."}

                      </p>

                    </div>


                  </div>

                );

              })()

            )}

          </section>

        )}

      </section>

    </div>
  );
}

export default App;