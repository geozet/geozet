/**
 * Copyright (c) 2010 PDOK
 *
 * Published under the Open Source GPL 3.0 license.
 * http://www.gnu.org/licenses/gpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */

/**
 * Class: Geozet.Control.ScaleBar
 * Customized scalebar control to meet the visual requirements 
 * of the Geozet project. The changes are:
 * - sequence of containers numbersContainer and graphicsContainer reversed
 * - add km/m label after the last number
 * - don't show the title
 * - take the margin into consideration when setting the height
 *
 * Inherits:
 *  - <OpenLayers.Control.ScaleBar>
 */
Geozet.Control.ScaleBar = OpenLayers.Class(OpenLayers.Control.ScaleBar, {

    /**
     * Constructor: Geozet.Control.ScaleBar
     *
     * Parameters:
     * options - {Object} Optional object whose properties will be set on this
     *     object.
     */
    initialize: function(options) {
        OpenLayers.Control.prototype.initialize.apply(this, [options]);
        if(!document.styleSheets) {
            this.limitedStyle = true;
        }
        if(this.limitedStyle) {
            this.appliedStyles = OpenLayers.Util.extend({}, this.defaultStyles);
            OpenLayers.Util.extend(this.appliedStyles, this.customStyles);
        }
        // create scalebar DOM elements
        this.element = document.createElement('div');
        this.element.style.position = 'relative';
        this.element.className = this.displayClass + 'Wrapper';
        this.labelContainer = document.createElement('div');
        this.labelContainer.className = this.displayClass + 'Units';
        this.labelContainer.style.position = 'absolute';
        this.graphicsContainer = document.createElement('div');
        this.graphicsContainer.style.position = 'absolute';
        this.graphicsContainer.className = this.displayClass + 'Graphics';
        this.numbersContainer = document.createElement('div');
        this.numbersContainer.style.position = 'absolute';
        this.numbersContainer.className = this.displayClass + 'Numbers';
        // GEOZET CHANGE 
        // reverse sequence of containers, we do not need the labelContainer
        this.element.appendChild(this.numbersContainer);
        this.element.appendChild(this.graphicsContainer);
        // END OF GEOZET CHANGE
    },

    /**
     * Method: draw
     * Draw the scalebar control.
     */   
    draw: function() {
        OpenLayers.Control.prototype.draw.apply(this, arguments);
        // determine offsets for graphic elements
        this.dxMarkerMajor = (
            this.styleValue('MarkerMajor', 'borderLeftWidth') +
            this.styleValue('MarkerMajor', 'width') +
            this.styleValue('MarkerMajor', 'borderRightWidth')
        ) / 2;
        this.dxMarkerMinor = (
            this.styleValue('MarkerMinor', 'borderLeftWidth') +
            this.styleValue('MarkerMinor', 'width') +
            this.styleValue('MarkerMinor', 'borderRightWidth')
        ) / 2;
        this.dxBar = (
            this.styleValue('Bar', 'borderLeftWidth') +
            this.styleValue('Bar', 'borderRightWidth')
        ) / 2;
        this.dxBarAlt = (
            this.styleValue('BarAlt', 'borderLeftWidth') +
            this.styleValue('BarAlt', 'borderRightWidth')
        ) / 2;
        this.dxNumbersBox = this.styleValue('NumbersBox', 'width') / 2;
        // set scale bar element height
        var classNames = ['Bar', 'BarAlt', 'MarkerMajor', 'MarkerMinor'];
        if(this.singleLine) {
            classNames.push('LabelBoxSingleLine');
        } else {
            classNames.push('NumbersBox', 'LabelBox');
        }
        var vertDisp = 0;
        // GEOZET CHANGE
        // add margin here (2 px)
        for(var classIndex = 0; classIndex < classNames.length; ++classIndex) {
            var cls = classNames[classIndex];
            vertDisp = Math.max(
                vertDisp,
                2+ this.styleValue(cls, 'top') + this.styleValue(cls, 'height')
            );
        }
        // END OF GEOZET CHANGE
        this.element.style.height = vertDisp + 'px';
        this.xOffsetSingleLine = this.styleValue('LabelBoxSingleLine', 'width') +
                                 this.styleValue('LabelBoxSingleLine', 'left');

        this.div.appendChild(this.element);
        this.map.events.register('moveend', this, this.onMoveend);
        this.update();
        return this.div;
    },

    /**
     * APIMethod: update
     * Update the scale bar after modifying properties.
     *
     * Parameters:
     * scale - {Float} Optional scale denominator.  If not specified, the
     *     map scale will be used.
     */
    update: function(scale) {
        if(this.map.baseLayer == null || !this.map.getScale()) {
            return;
        }
        this.scale = (scale != undefined) ? scale : this.map.getScale();
        // update the element title and width
        // GEOZET CHANGE
        // we do not want title
        //this.element.title = this.scaleText + OpenLayers.Number.format(this.scale);
        // END OF GEOZET CHANGE
        this.element.style.width = this.maxWidth + 'px';
        // check each measurement unit in the display system
        var comp = this.getComp();
        // get the value (subdivision length) with the lowest cumulative score
        this.setSubProps(comp);
        // clean out any old content from containers
        this.labelContainer.innerHTML = "";
        this.graphicsContainer.innerHTML = "";
        this.numbersContainer.innerHTML = "";
        // create all divisions
        var numDiv = this.divisions * this.subdivisions;
        var alignmentOffset = {
            left: 0 + (this.singleLine ? 0 : this.dxNumbersBox),
            center: (this.maxWidth / 2) -
                (numDiv * this.subProps.pixels / 2) -
                (this.singleLine ? this.xOffsetSingleLine / 2 : 0),
            right: this.maxWidth -
                (numDiv *this.subProps.pixels) -
                (this.singleLine ? this.xOffsetSingleLine : this.dxNumbersBox)
        }
        var xPos, measure, divNum, cls, left;
        for(var di=0; di<this.divisions; ++di) {
            // set xPos and measure to start of division
            xPos = di * this.subdivisions * this.subProps.pixels +
                   alignmentOffset[this.align];
            // add major marker
            this.graphicsContainer.appendChild(this.createElement(
                "MarkerMajor", " ", xPos - this.dxMarkerMajor
            ));
            // add major measure
            if(!this.singleLine) {
                measure = (di == 0) ? 0 :
                    OpenLayers.Number.format(
                        (di * this.subdivisions) * this.subProps.length,
                        this.subProps.dec, this.thousandsSeparator
                    );
                this.numbersContainer.appendChild(this.createElement(
                    "NumbersBox", measure, xPos - this.dxNumbersBox
                ));
            }
            // create all subdivisions
            for(var si=0; si<this.subdivisions; ++si) {
                if((si % 2) == 0) {
                    cls = "Bar";
                    left = xPos - this.dxBar;
                } else {
                    cls = "BarAlt";
                    left = xPos - this.dxBarAlt;
                }
                this.graphicsContainer.appendChild(this.createElement(
                    cls, " ", left, this.subProps.pixels
                ));
                // add minor marker if not the last subdivision
                if(si < this.subdivisions - 1) {
                    // set xPos and measure to end of subdivision
                    divNum = (di * this.subdivisions) + si + 1;
                    xPos = divNum * this.subProps.pixels +
                           alignmentOffset[this.align];
                    this.graphicsContainer.appendChild(this.createElement(
                        "MarkerMinor", " ", xPos - this.dxMarkerMinor
                    ));
                    if(this.showMinorMeasures && !this.singleLine) {
                        // add corresponding measure
                        measure = divNum * this.subProps.length;
                        this.numbersContainer.appendChild(this.createElement(
                            "NumbersBox", measure, xPos - this.dxNumbersBox
                        ));
                    }
                }
            }
        }
        // set xPos and measure to end of divisions
        xPos = numDiv * this.subProps.pixels;
        xPos += alignmentOffset[this.align];
        // add the final major marker
        this.graphicsContainer.appendChild(this.createElement(
            "MarkerMajor", " ", xPos - this.dxMarkerMajor
        ));
        // add final measure
        measure = OpenLayers.Number.format(
            numDiv * this.subProps.length,
            this.subProps.dec, this.thousandsSeparator
        );
        if(!this.singleLine) {
            // GEOZET CHANGE
            // add label here
            this.numbersContainer.appendChild(this.createElement(
                "NumbersBox", measure + ' ' + this.subProps.abbr, xPos - this.dxNumbersBox
            ));
            // END OF GEOZET CHANGE
        }
        // add content to the label element
        var labelBox = document.createElement('div');
        labelBox.style.position = 'absolute';
        var labelText;
        if(this.singleLine) {
            labelText = measure;
            labelBox.className = this.displayClass + 'LabelBoxSingleLine';
            labelBox.style.left = Math.round(
                xPos + this.styleValue('LabelBoxSingleLine', 'left')) + 'px';
        } else {
            labelText = '';
            labelBox.className = this.displayClass + 'LabelBox';
            labelBox.style.textAlign = 'center';
            labelBox.style.width = Math.round(numDiv * this.subProps.pixels) + 'px'
            labelBox.style.left = Math.round(alignmentOffset[this.align]) + 'px';
            labelBox.style.overflow = 'hidden';
        }
        if(this.abbreviateLabel) {
            labelText += ' ' + this.subProps.abbr;
        } else {
            labelText += ' ' + this.subProps.units;
        }
        labelBox.appendChild(document.createTextNode(labelText));
        this.labelContainer.appendChild(labelBox);
    },

    CLASS_NAME: "Geozet.Control.ScaleBar"

});
