import React, { useEffect, useState } from 'react';
import './SearchResponse.css';
import axios from 'axios';
import Cookies from 'universal-cookie';
import Popup from 'reactjs-popup';
import toast, { Toaster } from 'react-hot-toast';
import Select from 'react-select';
import Config  from '../../config/config';

function SearchResponse({seDate}) {
  const cookie = new Cookies();
  let token = cookie.get('token');

  const [isUpdated, setIsUpdated] = useState(false);
  const [isReady, setIsReady] = useState(false);
  const initialValues = { rooms: "", description: ""};
  const [formValues, setFormValues] = useState(initialValues);
  const [freeRooms, setFreeRooms] = useState('');

  //select multiple reservations
  const [selectedOption, setSelectedOption] = useState(null);
  const [optionSelectedList, setOptionSelectedList] = useState([]);

  useEffect(() => {
    console.log(seDate[0] + " - " + seDate[1]);
    axios({
      method: 'post',
      url:  `${Config.API_ADDR}/Benny/searchFreeRooms`,
      headers: {'Content-Type': 'application/json', 
              "Authorization": "Bearer " + token
      },
      data: JSON.stringify({
          start_date: seDate[0],
          end_date: seDate[1],
        })
      }).then((resp) => {
        setIsReady(true);
        setFreeRooms(JSON.stringify(resp.data))
        let options = [];
        JSON.parse(JSON.stringify(resp.data)).map(room => (
          options.push({value: room.id, label: room.number})
        ));
        setOptionSelectedList(options);
      }).catch((err) => {
        console.log(err);
      });
  }, [seDate, isUpdated, token]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({ ...formValues, [name]: value });
  };

  const handleSubmit = (roomParam, status, close) => (e) => {
    e.preventDefault();

    let stringRoomList = "";

    if (status) {
      roomParam.forEach(element => {
        stringRoomList += element.label + " ";
      });
    }

    toast.promise(
      axios({
        method: 'post',
        url: `${Config.API_ADDR}/Benny/addReservation`,
        headers: {'Content-Type': 'application/json', 
                "Authorization": "Bearer " + token
        },
        data: JSON.stringify({
          start_date:  seDate[0],
          end_date: dateFormat(getNextDate(seDate[1]).toLocaleDateString('en-UK')),
          rooms: status ? stringRoomList : roomParam.number,
          description: formValues.description
        })
      }).then(function (response) {
        if( response.data === "Rezervarea a fost adaugata") {
          setSelectedOption(null)
          setIsUpdated(!isUpdated);
          setFormValues({ ...formValues, rooms: '', description: '' });
          if (close && typeof(close) == 'function') {
            close();
          }
        }
      }).catch((err) => {
        console.log(err);
        setFormValues({ ...formValues, rooms: '', description: '' });
        if (close && typeof(close) == 'function') {
          close()
        }
      }),
      {
        loading: 'Se incarca...',
        success: 'Rezervarea a fost adaugata!',
        error: 'Eroare..Rezervarea nu s-a efectuat!',
      }
    )
  };

  const dateFormat = (value) => {
    let dateArr = value.split('/');
    return dateArr[2] + '-' +
          (dateArr[1].length === 1 ? ( '0' + dateArr[1]) : dateArr[1]) + '-' +
          (dateArr[0].length === 1 ? ( '0' + dateArr[0]) : dateArr[0]);
  };

  const getNextDate = ( currentDate ) => {
    let nextDate = new Date(currentDate);
    nextDate.setDate(nextDate.getDate() + 1);
    return nextDate;
  };

  return(
    <div className='align-items-center'>
      <div className='d-flex justify-content-center'>
        {isReady && freeRooms &&
          <Popup
            trigger={
              <button type="button" className='btn btn-success mb-4'>
                Rerzerva mai multe camere
              </button>} modal nested>
              {close => (
                <div className='modall'>
                  <button className="close" onClick={close}>
                    &times;
                  </button>
                  <div className="header">
                    Fa o rezervare
                  </div>
                  <div className="content">
                    <form onSubmit={handleSubmit(selectedOption, true, close)} className='form-login mt-10 justify-content-center'>
                      <div className="ui-form">

                        <div className='field' style={{paddingBottom: "20px"}}>
                          <Select
                            className='selector'
                            closeMenuOnSelect={false}
                            blurInputOnSelect={false}
                            options={optionSelectedList}
                            defaultValue={selectedOption}
                            onChange={setSelectedOption}
                            isSearchable={false}
                            isMulti
                            menuShouldBlockScroll={true}
                          />
                        </div>
                        <div className="field">
                          <input
                            autoComplete='off'
                            className='inputResrveModal'
                            type="text"
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
              )
            }
          </Popup>
        }
        <Toaster />
      </div>

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
                      <div className='modall'>
                        <button className="close" onClick={close}>
                          &times;
                        </button>
                        <div className="header">
                          <p>{seDate[0]} - {seDate[1]}</p>
                          <p>Camera {room.number}</p>
                        </div>
                        <div className="content">
                          <form onSubmit={handleSubmit(room, false)} className='form-login justify-content-center'>
                            <div className="ui-form">
                              <div className="field">
                                <input
                                  autoComplete='off'
                                  className='inputResrveModal'
                                  type="text"
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
                    )
                  } 
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

export default SearchResponse