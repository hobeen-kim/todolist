import { TopListContext } from '../../../store/toplist/topList-context';
import { useEffect, useState, useContext } from "react";
import TopList from './TopList';

const TopLists = ({categoryId}) => {
    
    const topListCtx = useContext(TopListContext);
    const [topLists, setTopLists] = useState([]);

    const [from, setFrom] = useState(null);
    const [to, setTo] = useState(null);
    const [isDone, setIsDone] = useState(false);

    useEffect(() => {
        topListCtx.getTopLists(categoryId, from, to, isDone);
    }, [categoryId]);

    useEffect(() => {
        setTopLists(topListCtx.topLists);
    }, [topListCtx.topLists]);
    
    return (
        <div>
            <h3>Top List</h3>
            <div className='category-container__item'>
                {topLists?.map((topList, index) => {
                    return <TopList topList={topList}/>
                })}
            </div>
        </div>
    );
}

export default TopLists;