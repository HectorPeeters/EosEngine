function init()
    parent:getPosition().x = 1

    print("Hello from lua!")
end

counter = 0

function update(delta)
    parent:setRotation(counter)

    counter = counter + delta
end