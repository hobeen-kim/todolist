import { TodoContext } from '../../../store/todo/todo-context';
import { useEffect, useState, useContext } from "react";

const Todos = ({categoryId}) => {

    const todoCtx = useContext(TodoContext);
    const [todos, setTodos] = useState([]);

    const [from, setFrom] = useState(null);
    const [to, setTo] = useState(null);
    const [searchType, setSearchType] = useState('START_DATE');

    useEffect(() => {
        todoCtx.getTodos(categoryId, from, to, searchType);
    }, [categoryId]);

    useEffect(() => {
        setTodos(todoCtx.todos);
    }, [todoCtx.todos]);

    return (

        <div>Todo</div>

    );
}

export default Todos;