import '../../../public/css/category/toplist.css'
import TopListDetail from './TopListDetail';
import { useState, useEffect } from 'react';

const TopList = ({topList}) => {

    const [showDetail, setShowDetail] = useState(false);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (event.target.closest('.top-list-detail-content') === null) {
                setShowDetail(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []); 

    const showDetailHandler = () => {
        setShowDetail(true);
    }

    const changeStatusHandler = () => {
        window.alert("change status");
    }
    
    return (
        <div className="top-list" key={topList.id}>
            <button className={`top-list__button top-list-${topList.status}`} onClick={changeStatusHandler}/>
            <div className="top-list-title" onClick={showDetailHandler}>{topList.title}</div>
            {showDetail && <TopListDetail/>}
        </div>

    );
}

export default TopList;