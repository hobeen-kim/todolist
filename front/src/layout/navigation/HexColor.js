import iro from '@jaames/iro';
import React, { useEffect, useRef } from 'react';

const HexColor = ({ setHexColor }) => {
    const colorPickerRef = useRef(null);

    useEffect(() => {
        var colorPicker = new iro.ColorPicker("#picker", {
            // Set the size of the color picker
            width: 200,
            // Set the initial color to pure red
            color: "#ffffff"
          });

        colorPicker.on("color:change", function(color) {
            setHexColor(color.hexString);
        });
    }, []);

    return (
        <>
        {/* <div ref={colorPickerRef}></div> */}
        <div id="picker"></div>
        </>
    );
}

export default HexColor;