from tkinter import *
from Q4 import *


orgCount = 0

def getWidget(row, col):
    # assume at most 1 widget in each grid
    for widget in window.grid_slaves():
        if int(widget.grid_info()["row"])==row and int(widget.grid_info()["column"])==col:
            return widget

def clearResults():
    # clear grids used to display results
    for widget in window.grid_slaves():
        # remove displayed ranges of addresses
        if int(widget.grid_info()["row"])>2 and int(widget.grid_info()["column"])==2:
            widget.grid_forget()
            continue
        # remove "Unallocated addresses"
        if int(widget.grid_info()["row"])==orgCount+3 and int(widget.grid_info()["column"])==1:
            widget.grid_forget()
    
    

window = Tk()
window.title("CS6102 Assignment Q4")
window.geometry('900x600')

txt = "Any address in the block (format: 10.111.11.32/14): "
lbl = Label(window, text=txt, font=("Arial", 12))
lbl.grid(column=0, row=0)
ent = Entry(window, font=("Arial", 12))
ent.grid(column=1, row=0)

def addOrg():
    global orgCount
    clearResults()
    orgCount += 1
    lbl = Label(window, text="Organization "+str(orgCount), font=("Arial", 12))
    lbl.grid(row=2+orgCount, column=0)
    ent = Entry(window, font=("Arial", 12))
    ent.grid(row=2+orgCount, column=1)
btn = Button(window, text="Add Organization", command=addOrg)
btn.grid(column=0, row=1)

def delOrg():
    global orgCount
    clearResults()
    for lbl in window.grid_slaves():
        if int(lbl.grid_info()["row"]) > orgCount+1:
            lbl.grid_forget()
    orgCount -= 1
btn = Button(window, text="Delete Organization", command=delOrg)
btn.grid(column=1, row=1)

def findAddr():
    clearResults()
    # get inputs
    addr = ent.get()
    numOfAddr = []
    for row in range(3, 3+orgCount):
        widget = getWidget(row, 1)
        numOfAddr.append(int(widget.get()))
    ranges = findAddressForEachOrg(addr, *numOfAddr)
    print(ranges)
    # display outputs
    for i in range(len(ranges)//2):
        txt = ranges[2*i] + ' - ' + ranges[2*i+1]
        lbl = Label(window, text=txt, font=("Arial", 12))
        lbl.grid(row=3+i, column = 2)
    lbl = Label(window, text="Unallocated: ", font=("Arial", 12))
    lbl.grid(row=2+len(ranges)//2, column=1)
    
btn = Button(window, text="Find Addresses", command=findAddr)
btn.grid(column=2, row=1)

lbl = Label(window, text="Organization", font=("Arial", 12), fg="red")
lbl.grid(column=0, row=2)
lbl = Label(window, text="No. of Addresses", font=("Arial", 12), fg="green")
lbl.grid(column=1, row=2)
lbl = Label(window, text="Address Range", font=("Arial", 12), fg="blue")
lbl.grid(column=2, row=2)


window.mainloop()
