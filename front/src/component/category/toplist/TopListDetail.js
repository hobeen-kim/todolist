import { TopListContext } from '../../../store/toplist/topList-context';
import { useEffect, useState, useContext } from "react";

const TopListDetail = ({topList, setShowDetail}) => {

    const topListCtx = useContext(TopListContext);
    const [title, setTitle] = useState(topList.title);
    const [content, setContent] = useState(topList.content);

    const updateTopListHandler = () => {
        topListCtx.updateTopListTitleContent(topList.id, title, content);
        setShowDetail(false);
    }

    const todoDoneStyle = (done) => {
        if(done) return {textDecoration: 'line-through'};
    }

    return (
        <div className="top-list-modal">
            <div className="top-list-detail-content">
                title : <input 
                        value={title} 
                        onChange={(e) => setTitle(e.target.value)}
                    />
                content : <input 
                        value={content} 
                        onChange={(e) => setContent(e.target.value)}
                    />

                todos :  <button>+ (추가는 나중에 구현)</button>
                {topList.todos.map(todo => {
                    return <div style={todoDoneStyle()}>{todo.content}</div>
                })
                }
                <button onClick={updateTopListHandler}>수정</button>
            </div>
        </div>
    );

}

export default TopListDetail;