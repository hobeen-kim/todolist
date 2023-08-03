import { useNavigate } from 'react-router-dom';
import { useContext } from 'react';
import { CategoryContext } from '../../store/category/category-context';

const CategoryButton = ({ categoryName, categoryId }) => {

    const navigate = useNavigate();
    const categoryCtx = useContext(CategoryContext);



    const move = () => {
        categoryCtx.setCategory(categoryId);
        navigate(`/categories/${categoryId}`);
    }

    return (
            <button class="menu__item active" onClick={move}>
            <h3>{categoryName}</h3>
            </button>   
    );
}

export default CategoryButton;