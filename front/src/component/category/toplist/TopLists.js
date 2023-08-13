import { TopListContext } from '../../../store/toplist/topList-context';
import { useEffect, useState, useContext } from "react";
import TopList from './TopList';
import TopListCreateModal from './TopListCreateModal';

const formatDate = (date) => {
    let year = date.getFullYear();
    let month = (date.getMonth() + 1).toString().padStart(2, '0'); // 월
    let day = date.getDate().toString().padStart(2, '0'); // 일

    return `${year}-${month}-${day}`;
}

const sevenDaysAgo = new Date();
sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
const initialFrom = formatDate(sevenDaysAgo);

const TopLists = ({categoryId}) => {
    
    const topListCtx = useContext(TopListContext);
    const [topLists, setTopLists] = useState([]);

    const [from, setFrom] = useState(initialFrom);
    const [to, setTo] = useState(null);
    const [containDone, setContainDone] = useState(true);
    const [isCreate, setIsCreate] = useState(false);

    useEffect(() => {
        topListCtx.getTopLists(categoryId, from, to, true);
    }, [categoryId]);

    useEffect(() => {
        if (containDone) {
            setTopLists(topListCtx.topLists);
        } else {
            const filteredTopLists = topListCtx.topLists.filter(topList => !topList.done);
            setTopLists(filteredTopLists);
        }
    }, [topListCtx.topLists, containDone]);

    useEffect(() => {

        const handleClickOutside = (event) => {
            if (event.target.closest('.top-list-create-content') === null) {
                setIsCreate(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []); 

    const handleContainDone = () => {
        setContainDone(!containDone);
    }

    const showCreateModal = () => {
        setIsCreate(true);
    }
        
    
    
    return (
        <div>
            <div className='top-lists-header'>
            <h3>Top List</h3>
            <div>
                <button value="button" className='top-list-isDone' onClick={handleContainDone}>완료 보기</button>
                <button className='top-lists-add' onClick={showCreateModal}>+</button>
                {isCreate && <TopListCreateModal categoryId={categoryId} setIsCreate={setIsCreate}/> }
            </div>
            </div>
            <div className='category-container__item'>
                
                {topLists?.map((topList) => {
                    return <TopList topList={topList}/>
                })}
            </div>
        </div>
    );
}

export default TopLists;