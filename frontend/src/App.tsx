
import { Navigate, Route, Routes } from 'react-router-dom'
import { Login } from './features/auth/login'
import { AppLayout } from './components/AppLayout'
import { RequireAuth } from './components/RequireAuth'
import Estoque from './features/stock/Estoque'
import Transferencias from './features/transfers/Transferencias'
import MatrizSalvacao from './features/salvation/MatrizSalvacao'
import Descartes from './features/descartes/Descartes'


function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />

      <Route element={<RequireAuth />}>
        <Route element={<AppLayout />}>
          <Route path="/estoque" element={<Estoque />} />
          <Route path="/transferencias" element={<Transferencias />} />
          <Route path="/matriz-salvacao" element={<MatrizSalvacao />} />
          <Route path="/descartes" element={<Descartes />} />
        </Route>
      </Route>
    </Routes>
  )
}

export default App
