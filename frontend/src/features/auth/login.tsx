import { useState } from 'react';
import loginBg from '../../assets/login.jpeg';
import './login.css';

export function Login() {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    console.log({ email, senha });
  };

  return (
    <main className="login-page">
      <section className="login-visual" aria-label="SustentaMed">
        <img className="login-visual-image" src={loginBg} alt="" />
        <div className="login-visual-overlay" />
        <div className="login-visual-content">
          <p className="login-visual-eyebrow">CUIDADO QUE CONECTA</p>
          <h1>SustentaMed</h1>
          <p className="login-visual-caption">
            Tecnologia e cuidado a serviço de uma saúde mais sustentável.
          </p>
          <span className="login-image-credit">SAÚDE · COMUNIDADE · FUTURO</span>
        </div>
      </section>

      <section className="login-panel">
        <div className="login-form-container">

          <header className="login-heading">
            <p className="login-kicker">BEM-VINDO(A) DE VOLTA</p>
            <h2>Entre na sua conta</h2>
            <p className="login-description">
              Informe seus dados para acessar a plataforma.
            </p>
          </header>

          <form onSubmit={handleLogin} className="login-form">
            <div className="login-field">
              <label htmlFor="login-email">E-mail</label>
              <input
                id="login-email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="seu.email@instituicao.com"
                autoComplete="email"
                required
              />
            </div>

            <div className="login-field">
              <label htmlFor="login-password">Senha</label>
              <input
                id="login-password"
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                placeholder="Digite sua senha"
                autoComplete="current-password"
                required
              />
            </div>

            <button type="submit" className="login-submit">
              <span>Entrar na plataforma</span>
              <span className="login-submit-arrow" aria-hidden="true">→</span>
            </button>
          </form>

          <p className="login-footer">ACESSO RESTRITO A PROFISSIONAIS AUTORIZADOS</p>
        </div>
      </section>
    </main>
  );
}
