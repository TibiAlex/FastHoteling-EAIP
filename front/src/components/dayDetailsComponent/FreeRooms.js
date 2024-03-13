import React, { useEffect, useState } from 'react'
import Cookies from 'universal-cookie';
import axios from 'axios';
import './DayDetails.css'
import Popup from 'reactjs-popup';
import toast, { Toaster } from 'react-hot-toast';
import Config  from '../../config/config';

function DayDetails({ date }) {
  const cookie = new Cookies();
  let token = cookie.get('token');

  const [freeRooms, setFreeRooms] = useState('');
  const [isReady, setIsReady] = useState(false);
  const [isUpdated, setIsUpdated] = useState(false);

  const initialValues = { room: '', description: ''};
  const [formValues, setFormValues] = useState(initialValues);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({ ...formValues, [name]: value });
  };

  const dateFormat = (value) => {
    let dateArr = value.split('/');
    return dateArr[2] + '-' +
          (dateArr[1].length === 1 ? ( '0' + dateArr[1]) : dateArr[1]) + '-' +
          (dateArr[0].length === 1 ? ( '0' + dateArr[0]) : dateArr[0]);
  };

  const getNextDate = ( currentDate ) => {
    let previwDate = new Date(currentDate);
    previwDate.setDate(previwDate.getDate() + 1);
    return previwDate;
  };

  const dateFormatJSON = (value) => {
    let dateArr = value.split('-');
    return dateArr[0] + '/' +
        (dateArr[1].length === 1 ? ( '0' + dateArr[1]) : dateArr[1]) + '/' +
        (dateArr[2].length === 1 ? ( '0' + dateArr[2]) : dateArr[2]);
  };

  useEffect(() => {
    axios({
      method: 'get',
      url: `${Config.API_ADDR}/Benny/getCurrentFreeRooms`,
      headers: {
        "Authorization": "Bearer " + token
      },
      params: {
        day: dateFormatJSON(date),
      }
    }).then((resp) => {
      setIsReady(true);
      setFreeRooms(JSON.stringify(resp.data));  
    }).catch((err) => {
      console.log(err);
    });
  }, [date, isUpdated, token])

  const handleSubmit = roomParam => (e) => {
    e.preventDefault();

    toast.promise(
      axios({
        method: 'post',
        url: `${Config.API_ADDR}/Benny/addReservation`,
        headers: {'Content-Type': 'application/json', 
                "Authorization": "Bearer " + token
        },
        data: JSON.stringify({
          start_date: date,
          end_date: dateFormat(getNextDate(date).toLocaleDateString('en-UK')),
          rooms: roomParam.number,
          description: formValues.description
        })
      }).then((resp) => {
        if(resp.data === "Rezervarea a fost adaugata") {
          setIsUpdated(!isUpdated)
        }
        setFormValues({ ...formValues, description: "" });
      }).catch((err) => {
        console.log(err);
        setFormValues({ ...formValues, description: "" });
      }),
      {
        loading: 'Se incarca...',
        success: 'Rezervarea a fost adaugata!',
        error: 'Eroare..Rezervarea nu s-a efectuat!',
      }
    )
  };
  
  return (
    <div className='align-items-center'>
      {isReady && freeRooms &&
        JSON.parse(freeRooms).map(room => (
          <div key={room.id}>
            <div className="card mt-1">
              <div className='d-flex col justify-content-around align-items-center'>
                <ul className="list-group list-group-flush">
                  <li className="list-group-item">Numar camera: {room.number}</li>
                  <li className="list-group-item">Numar locuri: {room.occupancy}</li>
                </ul>

                <Popup
                  trigger={
                    <button type="button" className="btn btn-success">
                        Rerzerva
                    </button>} modal nested>
                  {close => (
                    <div className='modall m-auto'>
                      <button className="close" onClick={close}>
                        &times;
                      </button>
                      <div className="header">
                          Fa o rezervare
                      </div>
                      <div className="content">
                        <form onSubmit={handleSubmit(room)} className='form-login justify-content-center'>
                          <div className="ui-form">
                            <div className="field">
                              <input
                                autoComplete='off'
                                type="text"
                                className='inputResrveModal'
                                name="description"
                                placeholder="Descriere"
                                value={formValues.description}
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
                  )}
                </Popup>
                <Toaster />
              </div>
            </div>
          </div>
        ))
      } 
    </div>
  )
}

export default DayDetails