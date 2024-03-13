import React, { useState } from 'react'
import DatePicker from 'react-datepicker';
import './Search.css'
import 'react-datepicker/dist/react-datepicker.css'
import SearchResponse from './SearchResponse';
import Navigation from '../navigationComponent/Navigation';

function Search() {
  const [dateRange, setDateRange] = useState([null, null]);
  const [startDate, endDate] = dateRange;
  const [seDate, setSeDate] = useState([null, null])
  const [isSubmit, setIsSubmit] = useState(false)

  const handleSubmit = (e) => {
    e.preventDefault();

    if(startDate != null && endDate != null) {
      var splitStartDate=startDate.toLocaleDateString('en-UK').split('/');
      var sdate = splitStartDate[2] +  "-" +
        (splitStartDate[1].length === 1 ? ( '0' + splitStartDate[1]) : splitStartDate[1]) + '-' +
        (splitStartDate[0].length === 1 ? ( '0' + splitStartDate[0]) : splitStartDate[0]);
      
      var splitEndDate=endDate.toLocaleDateString('en-UK').split('/');
      var edate = splitEndDate[2] +  "-" +
      (splitEndDate[1].length === 1 ? ( '0' + splitEndDate[1]) : splitEndDate[1]) + '-' +
      (splitEndDate[0].length === 1 ? ( '0' + splitEndDate[0]) : splitEndDate[0]);

      setSeDate([sdate, edate]);
      setIsSubmit(true);
    }
  }

  return (
    <div>
      <Navigation />
      <form onSubmit={handleSubmit} className='form-search'>
        <DatePicker
          className='search_style'
          selectsRange={true}
          startDate={startDate}
          endDate={endDate}
          onChange={(update) => {
            setDateRange(update);
            setIsSubmit(false)
          }}
          onChangeRaw={(e) => e.preventDefault()}
          onFocus={e => e.target.blur()}
          withPortal
          dateFormat='dd.MM.yyyy'
          disabledKeyboardNavigation
          isClearable={false}
        />
        <button className="btn btn-info my-3">
          Cauta
        </button>
      </form>

      {isSubmit && startDate && endDate &&
        <div className='mx-3 mt-2'>
          <SearchResponse seDate = {seDate}/>
        </div>
      }
    </div>
  );
}

export default Search