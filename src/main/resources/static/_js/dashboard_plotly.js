// +---------------------draw graph by plotly---------------------------+

getConversionRateGraph();
getDailyDeliveryRate();
getDailyEventCount(30);

async function getConversionRateGraph() {
    try {
        const response = await fetch('api/1.0/track/conversion', {
            method: 'GET',
            headers: headers
        });
        const data = await response.json();
        const expectedOrder = ["ALL", "RECEIVE", "OPEN", "CLICK", "FAILED"];
        const sortedData = expectedOrder.map(key => [key, data[key]]);

        const types = sortedData.map(pair => pair[0]);
        const count = sortedData.map(pair => pair[1]);
        // const types = Object.keys(data);
        // const count = Object.values(data);

        const plotData = [{
            x: types,
            y: count,
            type: 'bar'
        }];
        const layout = {
            title: 'History Conversion Rate',
            xaxis: {
                title: 'Type'
            },
            yaxis: {
                title: 'Event Count'
            }
        };
        Plotly.newPlot('conversion-rate', plotData, layout);
    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

async function getDailyDeliveryRate() {
    try {
        const response = await fetch('api/1.0/track/daily-delivery', {
            method: 'GET',
            headers: headers
        });
        const data = await response.json();
        const date = Object.keys(data);
        const count = Object.values(data);
        const traceData = {
            x: date,
            y: count,
            type: 'scatter'
        }
        const layout = {
            title: 'Daily Delivery Rate of Past 30 Days',
            xaxis: {
                title: 'Date'
            },
            yaxis: {
                title: 'Delivery Rate'
            }
        };
        var graphData = [traceData];
        Plotly.newPlot('daily-delivery', graphData, layout);
    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

async function getDailyEventCount(days) {
    try {
        const response = await fetch('api/1.0/track/daily-event?days=' + days, {
            method: 'GET',
            headers: headers
        });
        const data = await response.json();
        const receiveDates = Object.keys(data["RECEIVE"]);
        const receiveCounts = Object.values(data["RECEIVE"]);
        const failedDates = Object.keys(data["FAILED"]);
        const failCounts = Object.values(data["FAILED"]);
        const openDates = Object.keys(data["OPEN"])
        const openCounts = Object.values(data["OPEN"])
        const clickDates = Object.keys(data["CLICK"])
        const clickCounts = Object.values(data["CLICK"])

        let receiveEvent = {
            x: receiveDates,
            y: receiveCounts,
            type: 'scatter',
            name: 'RECEIVE'
        };
        let failEvent = {
            x: failedDates,
            y: failCounts,
            type: 'scatter',
            name: 'FAIL'
        };
        let openEvent = {
            x: openDates,
            y: openCounts,
            type: 'scatter',
            name: 'OPEN'
        };
        let clickEvent = {
            x: clickDates,
            y: clickCounts,
            type: 'scatter',
            name: 'CLICK'
        }
        const layout = {
            title: 'Daily Event Count of Past 30 Days',
            xaxis: {
                title: 'Date'
            },
            yaxis: {
                title: 'Event Count'
            }
        };

        const traceData = [receiveEvent, failEvent, openEvent, clickEvent]
        Plotly.newPlot('daily-event', traceData, layout);
    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

