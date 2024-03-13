import React, { useState, useEffect } from 'react'
import FreeRooms from './dayDetailsComponent/FreeRooms';
import OccupiedRooms from './dayDetailsComponent/OccupiedRooms';
import './Home.css'

function HomeButtons({ date }) {
  const [status, setStatus] = useState(0);

  useEffect(() => {
    setStatus(0);
  }, [date])

  return (
    <>
      <div className='d-flex justify-content-around'>
        <button
          onClick={() => setStatus(1)}
          type="button" 
        className="btn btn-success">
            Libere
        </button>
        <button
          onClick={() => setStatus(2)}
          type="button"
          className="btn btn-danger">
            Ocupate
        </button>
      </div>

      <div className='mx-3 mt-4'>
        {status === 1 && 
          <FreeRooms date={date} />}
        {status === 2 && 
          <OccupiedRooms date={date} />}
      </div>
    </>
  )
}

export default HomeButtons