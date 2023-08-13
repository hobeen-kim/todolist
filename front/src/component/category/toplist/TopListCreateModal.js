import { TopListContext } from '../../../store/toplist/topList-context';
import { useEffect, useState, useContext } from "react";

const TopListCreateModal = ({categoryId, setIsCreate}) => {

    const topListCtx = useContext(TopListContext);

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    const topListCreateHandler = () => {
        topListCtx.createTopList(categoryId, title, content);
        setIsCreate(false);
    }

    return (
        <div className="top-list-modal">
            <div className="top-list-create-content">
            <div>
                title : 
                <input 
                    value={title} 
                    onChange={(e) => setTitle(e.target.value)}
                />
            </div>
            <div>
                content : 
                
                <input 
                    value={content} 
                    onChange={(e) => setContent(e.target.value)}
                />
            </div>
            <button onClick={topListCreateHandler}>생성</button>
            <button onClick={() => setIsCreate(false)}>취소</button>
            </div>
        </div>
    );

}

export default TopListCreateModal;