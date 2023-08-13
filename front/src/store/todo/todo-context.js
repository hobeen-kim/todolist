import React, { createContext, useState } from 'react';
import * as authAction from '../auth/auth-action'; 
import * as todoAction from './todo-action';

export const TodoContext = createContext({
    todos: [],
    getTodos: (categoryId, from, to, searchType) => {}
});

export const TodoContextProvider = ({children}) => {

    const [todos, setTodos] = useState([]);

    const getTodos = (categoryId, from, to, searchType) => {

        const token = authAction.retrieveStoredToken();
        const data = todoAction.getTodosHandler(token, categoryId, from, to, searchType);
        data.then((result) => {
            if (result !== null && result.status === 200) {
                setTodos(result.data.data.todos);
            }else{
                window.alert(result.data.message);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }
    
    const contextValue = {
        todos,
        getTodos
    }

    return (
        <TodoContext.Provider value={contextValue}>
            {children}
        </TodoContext.Provider>
    );
}

export default TodoContext;