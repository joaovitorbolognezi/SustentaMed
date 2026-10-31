import { useState } from 'react';
import loginBg from '../../assets/login.jpeg';

// Estilos compartilhados entre os dois campos do formulário
const fieldClass = 'grid gap-[0.65rem]';

const labelClass = 'text-[0.8rem] font-semibold text-[#dce7df]';

const inputClass = [
  'box-border min-h-[3.5rem] w-full rounded-[0.8rem] border border-[#29342d] px-4',
  'bg-[rgba(17,25,20,0.78)] text-[0.88rem] text-[#f2f7f3] outline-none',
  'placeholder:text-[#65736a]',
  'transition-[border-color,box-shadow,background] duration-[160ms] ease-[ease]',
  'focus:border-[rgba(114,224,160,0.75)] focus:bg-[#111a14] focus:shadow-[0_0_0_3px_rgba(114,224,160,0.1)]',
  'motion-reduce:transition-none',
].join(' ');

export function Login() {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    console.log({ email, senha });
  };

  return (
    <main
      className={[
        'grid min-h-[100svh] grid-cols-[minmax(0,1fr)_minmax(0,1fr)] overflow-hidden',
        'text-left font-[family-name:var(--sans)] text-[#f2f7f3]',
        '[background:radial-gradient(ellipse_at_85%_12%,rgba(32,91,62,0.2),transparent_38%),#0b100e]',
        'max-[760px]:grid-cols-1 max-[760px]:overflow-auto',
      ].join(' ')}
    >
      {/* Lado visual */}
      <section
        aria-label="SustentaMed"
        className="relative isolate flex min-h-[100svh] items-center overflow-hidden p-[clamp(2rem,6vw,5.5rem)] max-[760px]:min-h-[35svh] max-[760px]:p-8"
      >
        <img
          className="absolute inset-0 -z-10 h-full w-full object-cover object-center"
          src={loginBg}
          alt=""
        />
        <div className="absolute inset-0 -z-10 h-full w-full [background:linear-gradient(180deg,rgba(5,14,9,0.08)_25%,rgba(5,14,9,0.88)_100%),linear-gradient(90deg,rgba(5,14,9,0.22),transparent_75%)]" />

        <div className="max-w-[34rem] translate-y-40">
          <p className="mb-[0.65rem] text-[0.68rem] font-bold tracking-[0.18em] text-[#a0f2bb]">
            CUIDADO QUE CONECTA
          </p>
          <h1 className="m-0 text-[length:clamp(2.5rem,5vw,4.5rem)] font-semibold leading-[1.05] tracking-[-0.06em] text-white max-[760px]:text-[2.4rem]">
            SustentaMed
          </h1>
          <p className="mt-4 max-w-[27rem] text-[1rem] leading-[1.7] text-[rgba(238,248,241,0.78)] max-[760px]:hidden">
            Tecnologia e cuidado a serviço de uma saúde mais sustentável.
          </p>
          <span className="mt-14 block text-[0.6rem] font-bold tracking-[0.18em] text-[rgba(238,248,241,0.62)] max-[760px]:hidden">
            SAÚDE · COMUNIDADE · FUTURO
          </span>
        </div>
      </section>

      {/* Painel do formulário */}
      <section className="grid min-h-[100svh] min-w-0 place-items-center p-[clamp(2rem,6vw,6rem)] max-[760px]:min-h-[auto] max-[760px]:px-6 max-[760px]:py-12">
        <div className="w-full max-w-[27rem]">
          <header className="mb-10 mt-12 max-[760px]:my-8">
            <p className="mb-[0.8rem] text-[0.62rem] font-bold tracking-[0.18em] text-[#72e0a0]">
              BEM-VINDO(A) DE VOLTA
            </p>
            <h2 className="m-0 text-[length:clamp(2rem,3.4vw,2.8rem)] font-semibold leading-[1.15] tracking-[-0.055em] text-[#f2f7f3]">
              Entre na sua conta
            </h2>
            <p className="mt-[0.85rem] text-[0.92rem] text-[#9ba9a1]">
              Informe seus dados para acessar a plataforma.
            </p>
          </header>

          <form onSubmit={handleLogin} className="grid gap-[1.4rem]">
            <div className={fieldClass}>
              <label htmlFor="login-email" className={labelClass}>
                E-mail
              </label>
              <input
                id="login-email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="seu.email@instituicao.com"
                autoComplete="email"
                required
                className={inputClass}
              />
            </div>

            <div className={fieldClass}>
              <label htmlFor="login-password" className={labelClass}>
                Senha
              </label>
              <input
                id="login-password"
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                placeholder="Digite sua senha"
                autoComplete="current-password"
                required
                className={inputClass}
              />
            </div>

            <button
              type="submit"
              className={[
                'mt-2 flex min-h-[3.6rem] cursor-pointer items-center justify-between',
                'rounded-[0.8rem] border border-[rgba(159,245,190,0.35)] px-[1.15rem]',
                'bg-[linear-gradient(110deg,#28764a,#3a9a60)] text-[0.88rem] font-bold text-[#f5fff8]',
                'transition-[filter,transform,box-shadow] duration-[160ms] ease-[ease]',
                'hover:-translate-y-px hover:brightness-[1.12] hover:shadow-[0_0.7rem_2rem_rgba(42,153,89,0.2)]',
                'focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-[3px] focus-visible:outline-[#a0f2bb]',
                'motion-reduce:transition-none',
              ].join(' ')}
            >
              <span>Entrar na plataforma</span>
              <span className="text-[1.2rem]" aria-hidden="true">
                →
              </span>
            </button>
          </form>

          <p className="mt-10 text-[0.55rem] font-bold tracking-[0.13em] text-[#65736a]">
            ACESSO RESTRITO A PROFISSIONAIS AUTORIZADOS
          </p>
        </div>
      </section>
    </main>
  );
}