import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'universal-cookie';
import Config  from '../../config/config';

function Navigation() {
  let navigate = useNavigate();

  const routeChange = (path) => {
    navigate(path)
  }

  const cookie = new Cookies();
  let token = cookie.get('token');
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    axios({
      method: 'get',
      url: `${Config.API_ADDR}/Benny/isAdmin`,
      headers: {'Content-Type': 'application/json', 
              "Authorization": "Bearer " + token
      }
    }).then(function (response) {
      setIsAdmin(response.data);
    })
  },[token])

  return (
    <div className='d-flex col'>
        <div className='col-4 text-center mt-3 mb-4'>
          <button
            onClick={() => routeChange('/home')}
            type="button" 
            className="btn btn-secondary">
              Acasa
          </button>
        </div>
        <div className='col-4 text-center mt-3 mb-4'>
          <button
            onClick={() => routeChange('/search')}
            type="button" 
            className="btn btn-secondary">
              Cauta
          </button>
        </div>
        {
          isAdmin && 
          <div className='col-4 text-center mt-3 mb-4'>
            <button
              onClick={() => routeChange('/admin')}
              type="button" 
              className="btn btn-secondary">
                Admin
            </button>
          </div>
        }
    </div>
  )
}

export default Navigation