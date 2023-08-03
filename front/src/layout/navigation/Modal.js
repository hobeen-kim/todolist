import React, { useState } from 'react';
import HexColor from './HexColor';

const Modal = ({ addCategory, setShowModal }) => {

    const [hexColor, setHexColor] = useState('#000000');
    const [inputValue, setInputValue] = useState('');  // inputValue 상태 추가

    const close = () => {
        setShowModal(false);
    }

    const addCategoryHandler = () => {
        addCategory(inputValue, hexColor);  // inputValue 상태를 함께 넘김
    }

    // 사용자가 입력을 하는 경우 inputValue 상태를 업데이트하는 함수
    const handleInputChange = (e) => {
        setInputValue(e.target.value);
    }

    return (
    <div className="modal">
        <div className="modal-content">
            <input type='text' value={inputValue} onChange={handleInputChange}/>
            <HexColor setHexColor={setHexColor}/>
            <button onClick={addCategoryHandler}>저장</button>
            <button onClick={close}>닫기</button>
        </div>
    </div>
    )
    
}

export default Modal;
