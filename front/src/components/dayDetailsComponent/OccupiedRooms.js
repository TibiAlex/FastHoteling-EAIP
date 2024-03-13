import React, { useEffect, useState } from 'react'
import Cookies from 'universal-cookie';
import axios from 'axios';
import './DayDetails.css'
import toast, { Toaster } from 'react-hot-toast';
import Edit from './Edit'
import Config  from '../../config/config';

function OccupiedRooms({ date }) {
  const cookie = new Cookies();
  let token = cookie.get('token');

  const [occupiedRooms, setOccupiedRooms] = useState('');
  const [isReady, setIsReady] = useState(false);
  const [isUpdated, setIsUpdated] = useState(false);

  const dateFormat = (currDate) => {
    currDate = currDate.substring(0, 10);
    let dateArr = currDate.split('-');

    return dateArr[2] + '/' + dateArr[1] + '/' + dateArr[0];
  };

  useEffect(() => {
    axios({
      method: 'get',
      url: `${Config.API_ADDR}/Benny/getCurrentReservations`,
      headers: {
        "Authorization": "Bearer " + token
      },
      params: {
        day: date,
      }
    }).then((resp) => {
      setIsReady(true);
      setOccupiedRooms(JSON.stringify(resp.data));  
    }).catch((err) => {
      console.log(err);
    });
  }, [date, isUpdated, token]);

  const handleDelete = (idRoom) => (e) => {
    toast.promise(
      axios({
        method: 'post',
        url: `${Config.API_ADDR}/Benny/deleteReservation`,
        headers: {
          "Authorization": "Bearer " + token
        },
        data: {
          id: idRoom,
        }
      }).then((resp) => {
        setIsUpdated(!isUpdated)
      }).catch((err) => {
        console.log(err);
      }),
      {
        loading: 'Se incarca...',
        success: 'Rezervarea a fost stearsa!',
        error: 'Eroare..Stergerea nu s-a efectuat!',
      }
    )
  };

  return (
    <div className='align-items-center mb-2'>
      {isReady && occupiedRooms &&
        JSON.parse(occupiedRooms).map(reservation => (
          <div key={reservation.id}>
            <div className="card mt-1">
              <div className='d-flex col align-items-center justify-content-between'>
                <ul className="list-group list-group-flush">
                  <li className="list-group-item">Descriere: <b>{reservation.description}</b></li>
                  <li className="list-group-item">Rezervare facuta de: <b>{reservation.user_name}</b></li>
                  <li className="list-group-item">
                    Perioada:{' '}
                    <b>
                      {dateFormat(reservation.start_date)} - {dateFormat(reservation.end_date)}
                    </b>
                  </li>
                  <li className="list-group-item">
                    <div className='d-flex flex-wrap'>
                      Camere:{' '} 
                      {reservation.rooms.map((room) => (
                        <div>
                          <b className='mx-1'>{room}</b>
                          <div className='break'></div>
                        </div> 
                      ))}
                    </div>
                  </li>
                </ul>
                <div>
                  <Edit reservation = {reservation} isUpdated={isUpdated} setIsUpdated={setIsUpdated}/>
                  <button 
                    onClick={handleDelete(reservation.id)}
                    className="btn btn-danger my-2">
                      Sterge
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))
      }
      <Toaster />
    </div>
  )
}

export default OccupiedRooms