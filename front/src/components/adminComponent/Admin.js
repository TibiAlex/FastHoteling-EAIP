import React, { useState } from 'react'
import './Admin.css'
import axios from 'axios';
import Cookies from 'universal-cookie';
import Navigation from '../navigationComponent/Navigation';
import Popup from 'reactjs-popup';
import toast, { Toaster } from 'react-hot-toast';
import Config  from '../../config/config';

function Admin() {
  const cookie = new Cookies();
  let token = cookie.get('token');

  const initialValues = { number: "", occupancy: ""};
  const [formValues, setFormValues] = useState(initialValues);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({ ...formValues, [name]: value });
  };

  const handleSubmit = (close) => (e) => {
    e.preventDefault();

    axios({
      method: 'post',
      url: `${Config.API_ADDR}/Benny/addRoom`,
      headers: {'Content-Type': 'application/json', 
              "Authorization": "Bearer " + token
      },
      data: JSON.stringify({
        number: formValues.number,
        occupancy: formValues.occupancy
      })
    }).then(function (response) {
      if (response.data === "The room has been added!") {
        setFormValues({ ...formValues, number: '', occupancy: '' });
        
        if (close && typeof(close) == 'function') {
          close();
        }

        toast.success("Camera a fost adaugata!");
      }
    }).catch((err) => {
      setFormValues({ ...formValues, number: '', occupancy: '' });
        
      if (close && typeof(close) == 'function') {
        close();
      }
      toast.error(err.response.data.message);
    })
  };

  return (
    <div>
      <Navigation />

      <div className='d-flex justify-content-center mt-4'>
        <Popup
          trigger={
            <button type="button" className='btn btn-success mt-3'>
              Adauga camera
            </button>} modal nested>
            {close => (
              <div className='modall'>
                <button className="close" onClick={close}>
                  &times;
                </button>
                <div className="header">
                  Adauga o camera
                </div>
                <div className="content">
                  <form onSubmit={handleSubmit(close)} className='form-login mt-10 justify-content-center'>
                    <div className="ui-form">
                      <div className='field'>
                        <input
                          required
                          autoComplete='off'
                          className='inputResrveModal'
                          type="number"
                          name="number"
                          placeholder="Numar Camera"
                          value={formValues.number}
                          onChange={handleChange}
                        />
                      </div>
                      <div className="field">
                        <input
                          required
                          autoComplete='off'
                          className='inputResrveModal'
                          type="number"
                          name="occupancy"
                          placeholder="Capacitate"
                          value={formValues.occupancy}
                          onChange={handleChange}
                        />
                      </div>
                      <button 
                        className="fluid ui btn btn-success">
                          Rezerva
                      </button>
                    </div>
                  </form>
                </div>
              </div>
            )
          }
        </Popup>
        <Toaster />
      </div>
    </div>
  )
}

export default Admin