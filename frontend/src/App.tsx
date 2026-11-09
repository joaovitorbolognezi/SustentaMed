
import { Navigate, Route, Routes } from 'react-router-dom'
import { Login } from './features/auth/login'
import { AppLayout } from './components/AppLayout'
import Estoque from './features/stock/Estoque'
import Transferencias from './features/transfers/Transferencias'
import MatrizSalvacao from './features/salvation/MatrizSalvacao'


function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />

      <Route element={<AppLayout />}>
        <Route path="/estoque" element={<Estoque />} />
        <Route path="/transferencias" element={<Transferencias />} />
        <Route path="/matriz-salvacao" element={<MatrizSalvacao />} />
      </Route>
    </Routes>
  )
}

export default App
