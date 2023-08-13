import React, { createContext, useState } from 'react';
import * as authAction from '../auth/auth-action'; 
import * as topListAction from './topList-action';

export const TopListContext = createContext({
    topLists: [],
    getTopLists: (categoryId, from, to, isDone) => {},
    createTopList: (categoryId, title, content) => {},
    updateTopListStatus: (topListId, status) => {},
    updateTopListTitleContent: (topListId, title, content) => {},
});

export const TopListContextProvider = ({children}) => {

    const [topLists, setTopLists] = useState([]);

    const getTopLists = (categoryId, from, to, isDone) => {

        const token = authAction.retrieveStoredToken();
        const data = topListAction.getTopListHandler(token, categoryId, from, to, isDone);
        data.then((result) => {
            if (result !== null && result.data.code === 200) {
                setTopLists(result.data.data.topLists);
            }else{
                window.alert(result.data.message);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }

    const createTopList = (categoryId, title, content) => {
        const token = authAction.retrieveStoredToken();
        const data = topListAction.createTopListHandler(token, categoryId, title, content);
        data.then((result) => {
            if (result !== null && result.status === 201) {
                
                let location = result.headers['location'];
                let splitLocation = location.split("/");  // ["", "v1", "api", "categories", "5"]
                let id = splitLocation[splitLocation.length - 1];
                
                const newTopList = {
                    id: id,
                    title: title,
                    content: content,
                    status: "NOT_STARTED",
                    doneDate: null,
                    todos : [],
                    done: false,    
                    categoryId : categoryId,
                }


                setTopLists(prevTopLists => [...prevTopLists, newTopList]);
            }else{
                window.alert(result.data.message);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }

    const formatDate = (date) => {
        let year = date.getFullYear().toString().substr(-2); // 연도의 마지막 두 자리
        let month = (date.getMonth() + 1).toString().padStart(2, '0'); // 월
        let day = date.getDate().toString().padStart(2, '0'); // 일
    
        return `${year}.${month}.${day}`;
    }

    const updateTopListStatus = (topListId, status) => {
        const token = authAction.retrieveStoredToken();
        const data = topListAction.updateTopListHandler(token, topListId, null, null, null, status);
        data.then((result) => {
            if (result !== null && result.status === 204) {
                setTopLists(prevTopLists => prevTopLists.map(item => 
                    item.id === topListId
                        ? {
                            ...item, 
                            status: status,
                            done: status === "COMPLETED" ? true : false,
                            doneDate: status === "COMPLETED" ? formatDate(new Date()) : null
                          }
                        : item
                ))
            }else{
                window.alert(result.data.code);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }

    const updateTopListTitleContent = (topListId, title, content) => {
        const token = authAction.retrieveStoredToken();
        const data = topListAction.updateTopListHandler(token, topListId, title, content, null, null);
        data.then((result) => {
            if (result !== null && result.status === 204) {
                setTopLists(prevTopLists => prevTopLists.map(item =>
                    item.id === topListId
                        ? {
                            ...item,
                            title: title,
                            content: content
                        }
                        : item
                ))
            }else{
                window.alert(result.data.code);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }

    
    const contextValue = {
        topLists,
        getTopLists,
        createTopList,
        updateTopListStatus,
        updateTopListTitleContent
    }

    return (
        <TopListContext.Provider value={contextValue}>
            {children}
        </TopListContext.Provider>
    );
}

export default TopListContext;