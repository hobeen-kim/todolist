import CategoryButton from './CategoryButton';
import Modal from './Modal';
import '../../public/css/navigation/navigation.css';
import '../../public/js/navigation.js'
import { useEffect, useState, useContext } from 'react';
import CategoryContext from '../../store/category/category-context';
import AuthContext from '../../store/auth/auth-context';



const MainNavigation = () => {

    const [showModal, setShowModal] = useState(false);
    const [categories, setCategories] = useState([]);
    const categoryCtx = useContext(CategoryContext);
    const authCtx = useContext(AuthContext);

    useEffect(() => {
        setCategories(categoryCtx.categories);
    }, [categoryCtx.categories]);

    useEffect(() => {
        categoryCtx.getCategories();
    }, [authCtx.isLoggedIn]);

    useEffect(() => {
        const body = document.body;
        const menu = body.querySelector(".menu");
        const menuBorder = menu.querySelector(".menu__border");
        const menuItems = [...menu.querySelectorAll(".menu__item")];
        let activeItem = menu.querySelector(".active");

        if(menuItems.length > 0) {


            menuItems.forEach((item) => {
                item.classList.remove("active");
            })
            menuItems[0].classList.add("active");
            body.style.backgroundColor = categories[0]["hexColor"];

        }

        function clickItem(item, index) {

            menu.style.removeProperty("--timeOut");
            
            if (activeItem == item) return;
            
            if (activeItem) {
                activeItem.classList.remove("active");
            }

            
            item.classList.add("active");
            body.style.backgroundColor = categories[index]["hexColor"];
            activeItem = item;
            offsetMenuBorder(activeItem, menuBorder);
            
            
        }

        function offsetMenuBorder(element, menuBorder) {

            const offsetActiveItem = element.getBoundingClientRect();
            const left = Math.floor(offsetActiveItem.left - menu.offsetLeft - (menuBorder.offsetWidth  - offsetActiveItem.width) / 2) +  "px";
            menuBorder.style.transform = `translate3d(${left}, 0 , 0)`;

        }

        if(menuItems.length > 0) {

            offsetMenuBorder(activeItem, menuBorder);

            menuItems.forEach((item, index) => {

                item.addEventListener("click", () => clickItem(item, index));
                
            })

            window.addEventListener("resize", () => {
                offsetMenuBorder(activeItem, menuBorder);
                menu.style.setProperty("--timeOut", "none");
            });
        }

    }, [categories]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (event.target.closest('.modal-content') === null) {
                setShowModal(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []); 

    
    const addCategory = (categoryName, hexColor) => {

        categoryCtx.createCategory(categoryName, hexColor);
    }

    const show = () => {
        setShowModal(true);
    }
    

    return (
        <header className="navigation">
            <menu class="menu">

                {categories?.map((category, index) => {
                    return <CategoryButton key={index} categoryName={category["categoryName"]} categoryId={category["id"]} />
                })}
                <button class="menu__item__add" onClick={show}><h3>추가</h3></button>
                {showModal && <Modal className="category-modal" addCategory={addCategory} setShowModal={setShowModal} />}
                <div class="menu__border"></div>
            </menu>
            <div class="svg-container">
                <svg viewBox="0 0 202.9 45.5" >
                <clipPath id="menu" clipPathUnits="objectBoundingBox" transform="scale(0.0049285362247413 0.021978021978022)">
                    <path  d="M6.7,45.5c5.7,0.1,14.1-0.4,23.3-4c5.7-2.3,9.9-5,18.1-10.5c10.7-7.1,11.8-9.2,20.6-14.3c5-2.9,9.2-5.2,15.2-7
                    c7.1-2.1,13.3-2.3,17.6-2.1c4.2-0.2,10.5,0.1,17.6,2.1c6.1,1.8,10.2,4.1,15.2,7c8.8,5,9.9,7.1,20.6,14.3c8.3,5.5,12.4,8.2,18.1,10.5
                    c9.2,3.6,17.6,4.2,23.3,4H6.7z"/>
                </clipPath>
                </svg>
            </div>
        </header>
    );
}

export default MainNavigation;