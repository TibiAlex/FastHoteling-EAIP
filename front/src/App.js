import { BrowserRouter as Router, 
                          Routes, 
                          Route,
                          Navigate } from 'react-router-dom';
import './App.css';
import Home from './components/Home';
import Login from './components/loginComponent/Login';
import Search from './components/searchComponent/Search';
import Admin from './components/adminComponent/Admin';
import Cookies from 'universal-cookie';

function App() {

  const cookie = new Cookies();
  var count= Object.keys(cookie.getAll()).length;

  return (
    <div>
      <Router>
        <Routes>
          <Route path='/' element={(count === 0) ? <Navigate to='login'/> : <Navigate to='home'/>} />
          <Route path='/home' element={<Home />} />
          <Route path='/login' element={<Login />} />
          <Route path='/search' element={<Search />} />
          <Route path='/admin' element={<Admin />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
