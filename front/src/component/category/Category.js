import { useEffect, useContext, useState } from "react";
import { CategoryContext } from '../../store/category/category-context';
import '../../public/css/category/category.css';
import DayPlan from './dayplan/DayPlan';
import Todos from './todo/Todos';
import TopLists from './toplist/TopLists';
import WeekPlan from "./dayplan/WeekPlan";


const Category = () => {

    const categoryCtx = useContext(CategoryContext);
    const [category, setCategory] = useState(null);

    useEffect(() => {
        setCategory(categoryCtx.currentCategory);
    }, [categoryCtx.currentCategory]);


    return (
        <div className="category-container">
            <TopLists categoryId={category?.id} />
            <DayPlan />
            <Todos categoryId={category?.id} />
            <WeekPlan />
        </div>
    )
}

export default Category;