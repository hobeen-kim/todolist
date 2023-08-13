import '../../../public/css/category/toplist.css'
import TopListDetail from './TopListDetail';
import { TopListContext } from '../../../store/toplist/topList-context';
import { CategoryContext } from '../../../store/category/category-context';
import { useState, useEffect, useContext } from 'react';

const TopList = ({topList}) => {

    const topListCtx = useContext(TopListContext);
    const categoryCtx = useContext(CategoryContext);
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
        let status = topList.status;
        if(status === 'NOT_STARTED'){
            status = 'IN_PROGRESS';
        }else if(status === 'IN_PROGRESS'){
            status = 'COMPLETED';
        }else{
            status = 'NOT_STARTED';
        }
        topListCtx.updateTopListStatus(topList.id, status);
    }

    const dynamicStyle = () => {
        switch(topList.status) {
            case 'NOT_STARTED':
                return { backgroundColor: 'transparent', border: '0.1rem solid #ffffff' };
            case 'IN_PROGRESS':
                return { backgroundColor: 'transparent', border: `0.2rem solid ${categoryCtx.currentCategory.hexColor}` };
            case 'COMPLETED':
                return { backgroundColor: categoryCtx.currentCategory.hexColor, border: `0.1rem solid ${categoryCtx.currentCategory.hexColor}` };
            default:
                return {};
        }
    };

    const dynamicTitle = () => {
        if(topList.status === 'COMPLETED'){
            return {textDecoration: 'line-through'};
        }
    };
    
    return (
        <div className="top-list" key={topList.id}>
            <button className="top-list__button"
            style={dynamicStyle()}
             onClick={changeStatusHandler}/>
            <span className="top-list-title" onClick={showDetailHandler} style={dynamicTitle()}>{topList.title}</span>
            <span>{topList.doneDate}</span>
            {showDetail && <TopListDetail topList={topList} setShowDetail={setShowDetail}/>}
        </div>

    );
}

export default TopList;