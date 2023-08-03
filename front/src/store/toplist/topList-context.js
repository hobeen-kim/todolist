import React, { createContext, useState } from 'react';
import * as authAction from '../auth/auth-action'; 
import * as topListAction from './topList-action';

export const TopListContext = createContext({
    topLists: [],
    getTopLists: (categoryId, from, to, isDone) => {},
});

export const TopListContextProvider = ({children}) => {

    const [topLists, setTopLists] = useState([]);

    const getTopLists = (categoryId, from, to, isDone) => {

        console.log('TopListContextProvider');
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

    const contextValue = {
        topLists,
        getTopLists
    }

    return (
        <TopListContext.Provider value={contextValue}>
            {children}
        </TopListContext.Provider>
    );
}

export default TopListContext;