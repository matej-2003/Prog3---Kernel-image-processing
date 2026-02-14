<!-- The program should support different operations such as
edge detection, 
sharpen,
blur
mirror.


Implementation guidelines -->

# Running the program:
- User can specify the input image or folder with images.
- User can specify the sequence of operations to be executed on the images.
- Basic GUI to select images and operations should be implemented.
- The project must include a few sample images of different sizes.
- The program measures run-time needed to complete.


- The implementation must adapt automatically to the hardware it is being ran on (Physical CPU's, Cores, Memory, etc..).

<!-- # Testing
Testing can be done without kip.gui.GUI.

All three implementations should be tested.
The parameter that influences the run-time is the size of the image.
All three implementations should be tasted using the same image.
The test should be prepared by supplying different image sizes (not necessarily pretty images).
Images can also be randomly generated with a specified size parameter.
Each image is a new configuration, there should be at least 10 configurations of different sizes to obtain a representative sample of results.
Present the results in your report using numeric and graphical representation. Discuss the results and explain them in detail. -->

# Tests

`bash
convert dice.png -define convolve:scale=1 -convolve "-1,-1,-1 -1,8,-1 -1,-1,-1" output.jpg
`


I tried this for mona lisa image but i do not get the same result as returned by my program. 

## Image source: 
- ![PNG Transparency Demo](https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png)
- ![Guggenheim Artwork](https://www.guggenheim.org/wp-content/uploads/1946/01/76.2553.147_ph_web-1.jpg)
- ![Mona Lisa - Leonardo da Vinci](https://upload.wikimedia.org/wikipedia/commons/6/6a/Mona_Lisa,_by_Leonardo_da_Vinci,_from_C2RMF_retouched.jpg)
- ![Sliced Orange Fruit](https://unsplash.com/photos/sliced-orange-fruit-on-white-surface-bogrLtEaJ2Q)
- ![Body of Water Surrounded by Trees](https://unsplash.com/photos/body-of-water-surrounded-by-trees-NRQV-hBF10M)
- ![Silhouette Trees Against Night Sky](https://unsplash.com/photos/silhouette-trees-against-a-colorful-night-sky-2F-m1FvoJT4)
- ![Rugged Mountain Peaks](https://unsplash.com/photos/rugged-mountain-peaks-under-a-cloudy-sky-yPXr1TdNL-U)
- ![Snowy Mountain Peak](https://unsplash.com/photos/snowy-mountain-peak-under-a-clear-blue-sky-l6x954tJsaA)