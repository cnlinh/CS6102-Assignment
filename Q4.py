import math
#Q4a

#Convert 0.0.0.0/0 form to binary 
def convertIPv4ToBinary(address):
    try:
        addr_wo_prefix = address.split('/')[0]
        no = addr_wo_prefix.split('.')
        rs = ''
        rs = rs + bin(int(no[0]))[2:].zfill(8) 
        rs = rs + bin(int(no[1]))[2:].zfill(8) 
        rs = rs + bin(int(no[2]))[2:].zfill(8) 
        rs = rs + bin(int(no[3]))[2:].zfill(8)
        return rs
    except:
        print("Wrong address format")

#Convert binary to 0.0.0.0
def convertBinaryToIPv4(address):
    try:
        no1 = address[0:8]
        no2 = address[8:16]
        no3 = address[16:24]
        no4 = address[24:32]
        rs = str(int(no1, 2))
        rs = rs + '.' + str(int(no2, 2))
        rs = rs + '.' + str(int(no3, 2))
        rs = rs + '.' + str(int(no4, 2))
        return rs
    except:
        print("Wrong address format")

#Find first address from 0.0.0.0/0
def findFirstAddress(address):
    try:
        addr_wo_prefix = address.split('/')[0]
        prefix = int(address.split('/')[1])
        bin_addr = convertIPv4ToBinary(addr_wo_prefix)
        network = bin_addr[0:prefix]
        bin_rs = network
        for i in range(0, 32-prefix):
            bin_rs += '0'
        rs = convertBinaryToIPv4(bin_rs)
        return rs
    except:
        print("Wrong address format")

#Find last address from 0.0.0.0/0
def findLastAddress(address):
    try:
        addr_wo_prefix = address.split('/')[0]
        prefix = int(address.split('/')[1])
        bin_addr = convertIPv4ToBinary(addr_wo_prefix)
        network = bin_addr[0:prefix]
        bin_rs = network
        for i in range(0, 32-prefix):
            bin_rs += '1'
        rs = convertBinaryToIPv4(bin_rs)
        return rs
    except:
        print("Wrong address format")
    
print(convertIPv4ToBinary("10.111.11.32/14"))
print(findFirstAddress("10.111.11.32/14"))
print(findLastAddress("10.111.11.32/14"))

#Q4b
#Find no of addresses from 0.0.0.0/0
def findNoAddresses(address):
    try:
        prefix = int(address.split('/')[1])
        return 2**(32-prefix)
    except:
        print("Wrong address format")

print(findNoAddresses("10.111.11.32/14"))

#Return a tuple for range of each organisation with specified number of addresses
def findAddressForEachOrg(address, *noOfAddr):
    first_addr = findFirstAddress(address)
    last_addr = findLastAddress(address)
    temp_first = first_addr
    temp_last = first_addr
    rs = ()
    for no in noOfAddr:
        no = 2**math.ceil(math.log(no,2))
        rs = rs + (temp_first,)

        improper_last_bin = bin(int(convertIPv4ToBinary(temp_first),2) + no - 1)
        proper_last_bin = str(improper_last_bin)[2:].zfill(32)
        temp_last = convertBinaryToIPv4(proper_last_bin)
        
        rs = rs + (temp_last,)
        improper_first_bin = bin(int(convertIPv4ToBinary(temp_last),2) + 1)
        proper_first_bin = str(improper_first_bin)[2:].zfill(32)
        temp_first = convertBinaryToIPv4(proper_first_bin)

    #Unallocated addresses range
    rs = rs + (temp_first,)
    rs = rs + (last_addr,)
    return rs

print(findAddressForEachOrg("10.111.11.32/14", 100, 1, 10000))
        
        
