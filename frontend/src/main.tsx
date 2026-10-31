import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { Login } from './features/auth/login'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Login />
  </StrictMode>,
)
