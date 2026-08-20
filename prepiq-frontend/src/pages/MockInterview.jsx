import { useState } from 'react';
import { startInterview, submitAnswer, endInterview } from '../api/interviewApi';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

function MockInterview() {
  const [stage, setStage] = useState('idle'); // idle | loading | active | evaluating | finished
  const [sessionId, setSessionId] = useState(null);
  const [question, setQuestion] = useState(null);
  const [answer, setAnswer] = useState('');
  const [lastEvaluation, setLastEvaluation] = useState('');
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  const handleStart = async () => {
    setError('');
    setStage('loading');
    try {
      const data = await startInterview();
      setSessionId(data.sessionId);
      setQuestion(data.question);
      setLastEvaluation('');
      setStage('active');
    } catch (err) {
      setError('Could not start interview. The AI service may be busy — please try again.');
      setStage('idle');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!answer.trim()) return;
    setError('');
    setStage('evaluating');
    try {
      const data = await submitAnswer(sessionId, answer);
      setLastEvaluation(data.evaluation);
      setAnswer('');

      if (data.sessionComplete) {
        const finalResult = await endInterview(sessionId);
        setResult(finalResult);
        setStage('finished');
      } else {
        setQuestion(data.nextQuestion);
        setStage('active');
      }
    } catch (err) {
      setError('Something went wrong evaluating your answer. Please try again.');
      setStage('active');
    }
  };

  const handleRestart = () => {
    setStage('idle');
    setSessionId(null);
    setQuestion(null);
    setAnswer('');
    setLastEvaluation('');
    setResult(null);
  };

  return (
    <div className="page-flex">
      <Navbar />
      <div className="page-content page-flex-body">
        <h1>AI Mock Interview</h1>
        {error && <div className="alert alert-error" style={{ marginTop: '1rem' }}>{error}</div>}

        {stage === 'idle' && (
          <div className="card" style={{ marginTop: '1.5rem' }}>
            <h3 style={{ marginBottom: '0.5rem' }}>Ready for a mock interview?</h3>
            <p className="link-muted" style={{ marginBottom: '1.5rem' }}>
              Questions will target your weak topics from the dashboard. You'll get 4 questions with instant AI feedback, plus a final score.
            </p>
            <button className="btn btn-primary" style={{ width: 'auto', padding: '0.7rem 1.5rem' }} onClick={handleStart}>
              Start Interview
            </button>
          </div>
        )}

        {stage === 'loading' && (
          <div className="card" style={{ marginTop: '1.5rem' }}>
            <p className="link-muted">Preparing your first question...</p>
          </div>
        )}

        {(stage === 'active' || stage === 'evaluating') && question && (
          <div className="card" style={{ marginTop: '1.5rem' }}>
            {lastEvaluation && (
              <div className="alert alert-success" style={{ marginBottom: '1.2rem' }}>
                <strong>Previous answer feedback:</strong> {lastEvaluation}
              </div>
            )}
            <span className="chip" style={{ marginBottom: '0.8rem', display: 'inline-block' }}>
              Question {question.orderIndex} of 4 · {question.topic}
            </span>
            <p style={{ fontSize: '1.05rem', lineHeight: '1.5', marginBottom: '1.2rem' }}>{question.questionText}</p>
            <form onSubmit={handleSubmit}>
              <textarea
                className="field-input"
                style={{ minHeight: '120px' }}
                placeholder="Type your answer here..."
                value={answer}
                onChange={(e) => setAnswer(e.target.value)}
                disabled={stage === 'evaluating'}
                required
              />
              <button className="btn btn-primary" type="submit" disabled={stage === 'evaluating'}>
                {stage === 'evaluating' ? 'Evaluating...' : 'Submit Answer'}
              </button>
            </form>
          </div>
        )}

        {stage === 'finished' && result && (
          <div className="card" style={{ marginTop: '1.5rem', textAlign: 'center' }}>
            <p className="link-muted" style={{ marginBottom: '0.3rem' }}>Session Complete</p>
            <h2 style={{ fontSize: '2.5rem', margin: '0.5rem 0' }}>{result.score}/100</h2>
            <p style={{ marginTop: '1rem', lineHeight: '1.5' }}>{result.feedbackSummary}</p>
            <button className="btn btn-primary" style={{ width: 'auto', padding: '0.7rem 1.5rem', marginTop: '1.5rem' }} onClick={handleRestart}>
              Start Another Session
            </button>
          </div>
        )}
      </div>
      <Footer />
    </div>
  );
}

export default MockInterview;