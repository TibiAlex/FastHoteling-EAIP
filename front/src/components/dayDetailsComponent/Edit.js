import React, { useEffect, useState } from 'react';
import Cookies from 'universal-cookie';
import axios from 'axios';
import './DayDetails.css'
import Popup from 'reactjs-popup';
import DatePicker from 'react-datepicker';
import toast, { Toaster } from 'react-hot-toast';
import Select from 'react-select';
import './DayDetails.css'
import Config  from '../../config/config';

function Edit({reservation, isUpdated, setIsUpdated}) {
  const cookie = new Cookies();
  let token = cookie.get('token');

  const [dateRange, setDateRange] = useState([new Date(reservation.start_date), new Date(reservation.end_date)]);
  const [startDate, endDate] = dateRange;
  const [isSubmit, setIsSubmit] = useState(false)
  const [isReady, setIsReady] = useState(false);

  const initialValues = {description: reservation.description};
  const [formValues, setFormValues] = useState(initialValues);
  
  const transformArrRoomsInInputSelect = (rooms) => {
    let options = [];
    let id = 0;

    rooms.map(room => {
      options.push({value: id, label: room})
      id++;
    })

    return options;
  }

  //select multiple reservations
  const [selectedOption, setSelectedOption] = useState(transformArrRoomsInInputSelect(reservation.rooms));
  const [optionSelectedList, setOptionSelectedList] = useState([]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({ ...formValues, [name]: value });
  };

  useEffect(() => {
    if(startDate !== null && endDate !== null) {
      var splitStartDate=startDate.toLocaleDateString('en-UK').split('/');
      var sdate = splitStartDate[2] +  "-" +
      (splitStartDate[1].length === 1 ? ( '0' + splitStartDate[1]) : splitStartDate[1]) + '-' +
      (splitStartDate[0].length === 1 ? ( '0' + splitStartDate[0]) : splitStartDate[0]);
    
      var splitEndDate=endDate.toLocaleDateString('en-UK').split('/');
      var edate = splitEndDate[2] +  "-" +
      (splitEndDate[1].length === 1 ? ( '0' + splitEndDate[1]) : splitEndDate[1]) + '-' +
      (splitEndDate[0].length === 1 ? ( '0' + splitEndDate[0]) : splitEndDate[0]);
    
      axios({
        method: 'post',
        url: `${Config.API_ADDR}/Benny/searchFreeRooms`,
        headers: {'Content-Type': 'application/json', 
                "Authorization": "Bearer " + token
        },
        data: JSON.stringify({
            start_date: sdate,
            end_date: edate
        })
        }).then((resp) => {
          setIsReady(true);

          let options = [];
          JSON.parse(JSON.stringify(resp.data)).map(room => (
            options.push({value: room.id, label: room.number})
          ));
          setOptionSelectedList(options);
        }).catch((err) => {
          console.log(err);
        });
    }
  }, [isUpdated, token, reservation.rooms, reservation.start_date, reservation.end_date, startDate, endDate]);

  const handleEdit = (selectedOption, close) => (e) => {
    e.preventDefault();

    let stringRoomList = "";

    selectedOption.forEach(element => {
      stringRoomList += element.label + " ";
    });


    var splitStartDate=dateRange[0].toLocaleDateString('en-UK').split('/');
    var sdate = splitStartDate[2] +  "-" +
      (splitStartDate[1].length === 1 ? ( '0' + splitStartDate[1]) : splitStartDate[1]) + '-' +
      (splitStartDate[0].length === 1 ? ( '0' + splitStartDate[0]) : splitStartDate[0]);
    
    var splitEndDate=dateRange[1].toLocaleDateString('en-UK').split('/');
    var edate = splitEndDate[2] +  "-" +
    (splitEndDate[1].length === 1 ? ( '0' + splitEndDate[1]) : splitEndDate[1]) + '-' +
    (splitEndDate[0].length === 1 ? ( '0' + splitEndDate[0]) : splitEndDate[0]);

    console.log(sdate + " - " + edate);

    axios({
      method: 'post',
      url: `${Config.API_ADDR}/Benny/updateReservation`,
      headers: {'Content-Type': 'application/json', 
              "Authorization": "Bearer " + token
      },
      data: JSON.stringify({
        id: reservation.id,
        start_date:  sdate,
        end_date: edate,
        rooms: stringRoomList,
        description: formValues.description
      })
    }).then(function (response) {
      console.log(response.data)
      if( response.data === "Rezervarea a fost modificata") {
        setOptionSelectedList([]);
        setIsUpdated(!isUpdated)
        if (close && typeof(close) == 'function') {
          close();
        }
        toast.success("Rezervarea a fost actualizata!")
      }
    }).catch((err) => {
      console.log(err);
      if (close && typeof(close) == 'function') {
        close()
      }
      toast.error(err.response.data.message);
    })
  };

  return (
    <div>
      <Popup
        trigger={
          <button
            className="btn btn-warning my-2"
            style={{width: '75px'}}>
              Edit
          </button>} modal nested>
          {close => (
            <div className='modall'>
              <button className="close" onClick={close}>
                &times;
              </button>
              <div className="header">
                Editeaza rezervarea
              </div>
              <div className="content">
                <form onSubmit={handleEdit(selectedOption, close)} className='form-login mt-10 justify-content-center'>
                  <div className="ui-form">
                    <div className='field'>
                      <DatePicker
                        className='search_style'
                        selectsRange={true}
                        startDate={startDate}
                        endDate={endDate}
                        onChange={(update) => {
                          setDateRange(update);
                          setIsSubmit(false)
                          setIsUpdated(!isUpdated)
                        }}
                        onChangeRaw={(e) => e.preventDefault()}
                        disabledKeyboardNavigation
                        withPortal
                        dateFormat='dd.MM.yyyy'
                        isClearable={false}
                        onFocus={e => e.target.blur()}
                      />
                    </div>
                    <div className='field'>
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
                        placeholder='Descriere'
                        value={formValues.description}
                        onChange={handleChange}
                      />
                    </div>
                    <button 
                      className="fluid ui btn btn-success">
                        Editeaza
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
  )
};

export default Edit